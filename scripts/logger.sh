#!/bin/bash

source ../config.sh

# Функция ротации логов
rotate_logs() {
    if [ -f "$LOG_FILE" ]; then
        local size=$(stat -f%z "$LOG_FILE" 2>/dev/null || stat -c%s "$LOG_FILE")
        if [ "$size" -gt "$MAX_LOG_SIZE" ]; then
            for ((i=MAX_LOG_FILES-1; i>0; i--)); do
                if [ -f "${LOG_FILE}.$i" ]; then
                    mv "${LOG_FILE}.$i" "${LOG_FILE}.$((i+1))"
                fi
            done
            mv "$LOG_FILE" "${LOG_FILE}.1"
        fi
    fi
}

# Функция логирования
log() {
    local level="$1"
    local message="$2"
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    rotate_logs
    
    echo "[$timestamp][$level] $message" | tee -a "$LOG_FILE"
}

# Функции для разных уровней логирования
log_info() {
    log "$LOG_LEVEL_INFO" "$1"
}

log_error() {
    log "$LOG_LEVEL_ERROR" "$1"
}

log_debug() {
    if [ "${DEBUG:-false}" = "true" ]; then
        log "$LOG_LEVEL_DEBUG" "$1"
    fi
} 