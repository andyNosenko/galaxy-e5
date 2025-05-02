#!/bin/bash
set -euo pipefail

# === Настройки ===
DEFAULT_IP="192.168.1.5"
APK_DIR="../app_to_install"
LOG_FILE="logs/adb_menu.log"

# === Функция логирования ===
log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}
cat <<'EOF'
   _______  _______  _______  __      ____    ____
  /  _____||   ____||   ____||  |     \   \  /   /
 |  |  __  |  |__   |  |__   |  |      \   \/   /
 |  | |_ | |   __|  |   __|  |  |       \_    _/
 |  |__| | |  |____ |  |____ |  `----.    |  |
  \______| |_______||_______||_______|    |__|

   _______      ___       __          ___      ___   ___ ____    ____
  /  _____|    /   \     |  |        /   \     \  \ /  / \   \  /   /
 |  |  __     /  ^  \    |  |       /  ^  \     \  V  /   \   \/   /
 |  | |_ |   /  /_\  \   |  |      /  /_\  \     >   <     \_    _/
 |  |__| |  /  _____  \  |  `----./  _____  \   /  .  \      |  |
  \______| /__/     \__\ |_______/__/     \__\ /__/ \__\     |__|

  _______  _____
 |   ____|| ____|
 |  |__   | |__
 |   __|  |___ \
 |  |____  ___) |
 |_______||____/

EOF

log "🔄 Запуск ADB-меню"

# === Определение IP-адреса шлюза (если возможно) ===
GATEWAY_IP=$(netstat -rn | grep default | awk '{print $2}' | head -n 1)
ADB_IP="${GATEWAY_IP:-$DEFAULT_IP}"

# === Подключение по Wi-Fi ===
log "Подключение к ADB по Wi-Fi: $ADB_IP:5555..."
if adb connect "$ADB_IP:5555" >/dev/null; then
  log "✅ Успешное подключение к ADB по Wi-Fi."
else
  log "❌ Ошибка подключения к ADB по Wi-Fi."
  echo "Проверьте устройство и сеть."
  exit 1
fi

# === Меню ===
while true; do
  echo "=========================================="
  echo "           МЕНЮ ADB-СКРИПТА (Mac)"
  echo "=========================================="
  echo "1. Проверить ADB-соединение по USB"
  echo "2. Запустить adb_test.sh"
  echo "3. Сделать резервную копию (backupzip.sh)"
  echo "4. Восстановить из копии (restorezip.sh)"
  echo "5. Открыть ADB-консоль"
  echo "6. Установить все APK из: $APK_DIR"
  echo "0. Выход"
  echo "=========================================="
  read -rp "Выберите действие (0-6): " choice

  case $choice in
    1)
      log "Проверка ADB-соединения по USB..."
      if adb devices | grep -q "device$"; then
        log "✅ Устройство по USB подключено."
      else
        log "❌ Устройство по USB не обнаружено."
      fi
      read -rp "Нажмите Enter для продолжения..."
      ;;
    2)
      log "▶ Запуск adb_test.sh"
      ./adb_test.sh || log "❌ Ошибка при запуске adb_test.sh"
      read -rp "Нажмите Enter для продолжения..."
      ;;
    3)
      log "💾 Создание резервной копии..."
      ./backupzip.sh || log "❌ Ошибка при запуске backupzip.sh"
      read -rp "Нажмите Enter для продолжения..."
      ;;
    4)
      log "♻ Восстановление из резервной копии..."
      ./restorezip.sh || log "❌ Ошибка при запуске restorezip.sh"
      read -rp "Нажмите Enter для продолжения..."
      ;;
    5)
      log "🖥 Запуск ADB-консоли"
      bash
      ;;
    6)
      log "📦 Установка всех APK из $APK_DIR"
      for apk in "$APK_DIR"/*.apk; do
        apk_name=$(basename "$apk")
        log "➡ Установка: $apk_name"
        if adb install -g "$apk"; then
          log "✅ Установлен: $apk_name"
        else
          log "❌ Ошибка установки: $apk_name"
        fi
      done
      read -rp "Нажмите Enter для продолжения..."
      ;;
    0)
      log "🚪 Выход из меню"
      break
      ;;
    *)
      echo "Неверный выбор. Повторите попытку."
      ;;
  esac
done

log "✅ Скрипт завершён"
