@echo off
echo ========================================
echo   TaskFlow API - Demarrage
echo ========================================
echo.

echo Verification de Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERREUR] Docker n'est pas installe ou n'est pas dans le PATH
    echo Veuillez installer Docker Desktop depuis https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo Docker detecte !
echo.

echo Verification de docker-compose...
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [ERREUR] docker-compose n'est pas disponible
    pause
    exit /b 1
)

echo docker-compose detecte !
echo.

echo Demarrage des conteneurs...
docker-compose up -d

if errorlevel 1 (
    echo [ERREUR] Impossible de demarrer les conteneurs
    pause
    exit /b 1
)

echo.
echo ========================================
echo   TaskFlow API demarre avec succes !
echo ========================================
echo.
echo API disponible sur : http://localhost:8080
echo Swagger UI : http://localhost:8080/swagger-ui.html
echo.
echo Pour voir les logs :
echo   docker-compose logs -f api
echo.
echo Pour arreter l'application :
echo   docker-compose down
echo.
pause
