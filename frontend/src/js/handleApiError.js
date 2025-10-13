export function handleApiError(res, data) {
    let message = 'Ошибка при выполнении запроса';

    if (res.status === 400 && data?.violations?.length) {
    const v = data.violations.map(v => `${v.field}: ${v.message}`).join(', ');
    return `Ошибка валидации: ${v}`;
    }

    if (res.status === 403) {
    const msg = (data?.message || '').toLowerCase();

    if (msg.includes('invalid password')) return 'Неверный пароль';
    if (msg.includes('bad credentials')) return 'Неверный email или пароль';
    if (msg.includes('user not found')) return 'Пользователь не найден';
    if (msg.includes('access denied')) return 'Доступ запрещён';
    return 'Ошибка авторизации. Проверьте введённые данные.';
    }

    if (res.status === 404 || data?.code === 404) return 'Пользователь не найден';

    if (res.status === 409) return 'Такой пользователь уже существует';

    if (res.status === 500) return 'Внутренняя ошибка сервера. Попробуйте позже.';

    if (data?.message) return data.message;

    return message;
}