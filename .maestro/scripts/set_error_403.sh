#!/bin/bash
adb shell "mkdir -p /sdcard/Android/data/com.adriano.sharetheimage/files/; echo 'ERROR_403' > /sdcard/Android/data/com.adriano.sharetheimage/files/mock_mode"
