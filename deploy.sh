#!/bin/bash

# ===========================================
# Deploy Script for Flood Monitoring Backend (No Docker for App)
# ===========================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   Flood Monitoring Backend Deploy     ${NC}"
echo -e "${BLUE}========================================${NC}"

# Pull Code
echo -e "\n${YELLOW}[1/4] Pulling latest code from Git...${NC}"
git pull origin main

# Build
echo -e "\n${YELLOW}[2/4] Building Java application...${NC}"
if command -v mvn &> /dev/null; then
    mvn clean package -DskipTests
else
    # Fallback to wrapper if mvn is not installed globally
    echo "Maven not found, using ./mvnw..."
    chmod +x mvnw
    ./mvnw clean package -DskipTests
fi

# Stop existing application
echo -e "\n${YELLOW}[3/4] Stopping existing application...${NC}"
# Find PID of the running jar
PID=$(pgrep -f "backend-0.0.1-SNAPSHOT.jar")

if [ -n "$PID" ]; then
    echo -e "Killing existing process with PID: $PID"
    kill -9 $PID
    sleep 2
else
    echo "No running application found."
fi

# Start application
echo -e "\n${YELLOW}[4/4] Starting application...${NC}"
nohup java -jar target/backend-0.0.1-SNAPSHOT.jar > backend.log 2>&1 &
NEW_PID=$!

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}   Deploy completed successfully!      ${NC}"
echo -e "${GREEN}   PID: $NEW_PID                       ${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "Logs are being written to: ${BLUE}backend.log${NC}"
echo -e "View logs command: ${YELLOW}tail -f backend.log${NC}"
