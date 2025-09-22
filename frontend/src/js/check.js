const fetch = require('node-fetch'); // если Node <18, нужно установить через npm i node-fetch

const SMARTCAPTCHA_SERVER_KEY = "<ключ_сервера>";

async function checkCaptcha(token, userIp) {
  try {
    const params = new URLSearchParams({
      secret: SMARTCAPTCHA_SERVER_KEY,
      token: token,
      ip: userIp, // IP пользователя
    });

    const res = await fetch('https://smartcaptcha.yandexcloud.net/validate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString(),
    });

    if (!res.ok) {
      console.error(`Ошибка сервера SmartCaptcha: ${res.status} ${res.statusText}`);
      return false; // в случае ошибки сервера лучше блокировать или временно разрешить
    }

    const data = await res.json();
    return data.status === 'ok';
  } catch (err) {
    console.error('Ошибка при проверке капчи:', err);
    return false;
  }
}

// Пример использования
const token = "<токен>";
const userIp = "<IP_пользователя>";

checkCaptcha(token, userIp).then((passed) => {
  if (passed) {
    console.log("Капча пройдена");
  } else {
    console.log("Подозрение на робота");
  }
});
