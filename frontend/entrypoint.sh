#!/bin/sh

CONFIG_FILE=/usr/share/nginx/html/config.js

echo "Generating runtime configuration in $CONFIG_FILE"
cat <<EOF > $CONFIG_FILE
window.config = {
    VITE_APP_AUTH_URL: "${VITE_APP_AUTH_URL:-undefined}",
    VITE_APP_MANAGER_URL: "${VITE_APP_MANAGER_URL:-undefined}",
    SMART_CAPTCHA_CLIENT_KEY: "${SMART_CAPTCHA_CLIENT_KEY:-undefined}",
};
EOF

# ВАЖНО: запускаем переданную в CMD команду
exec "$@"
