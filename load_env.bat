@echo off
for /f "usebackq tokens=1,2 delims==" %%A in (`findstr /v "^#" .env`) do (
    set "%%A=%%B"
)
