#!/bin/bash

read -p "Enter Project Name: " PROJECT_NAME

if [ -z $PROJECT_NAME ]; then
    echo "Project name cannot be empty"
    exit 2
fi

read -p "Enter target version: " MCVER

if [ -z $MCVER ]; then
    echo "Target version cannot be empty"
    exit 2
fi

PROJECT_NAME_LOWER=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]')

PROJECT_NAME_TOKEN="<%PROJECT_NAME%>"
PROJECT_NAME_LOWER_TOKEN="<%PROJECT_NAME_LOWER%>"
MCVER_TOKEN="<%MCVER%>"

POM_FILE="./pom.xml"
PLUGIN_CONFIG="./src/main/resources/plugin.yml"
PACKAGE_BASE_PATH="./src/main/java/com/justintaddei"
MAIN_CLASS_PATH="$PACKAGE_BASE_PATH/templateplugin"
MAIN_CLASS="$MAIN_CLASS_PATH/TemplatePlugin.java"

# Rename Main Java Class
sed -i "s/$PROJECT_NAME_TOKEN/$PROJECT_NAME/" "$MAIN_CLASS"
sed -i "s/$PROJECT_NAME_LOWER_TOKEN/$PROJECT_NAME_LOWER/" "$MAIN_CLASS"

# Rename pom.xml
sed -i "s/$PROJECT_NAME_TOKEN/$PROJECT_NAME/" "$POM_FILE"
sed -i "s/$PROJECT_NAME_LOWER_TOKEN/$PROJECT_NAME_LOWER/" "$POM_FILE"
sed -i "s/$MCVER_TOKEN/$MCVER/" "$POM_FILE"

# Rename plugin.yml
sed -i "s/$PROJECT_NAME_TOKEN/$PROJECT_NAME/" "$PLUGIN_CONFIG"
sed -i "s/$PROJECT_NAME_LOWER_TOKEN/$PROJECT_NAME_LOWER/" "$PLUGIN_CONFIG"
sed -i "s/$MCVER_TOKEN/$(echo $MCVER | sed -nr 's/^([0-9]+\.[0-9]+)/\1/p')/" "$PLUGIN_CONFIG"

mv "$MAIN_CLASS" "$MAIN_CLASS_PATH/$PROJECT_NAME.java"
mv "$MAIN_CLASS_PATH" "$PACKAGE_BASE_PATH/$PROJECT_NAME_LOWER"

LATEST_BUILD=$(curl -s https://papermc.io/api/v2/projects/paper/versions/"$MCVER"/ | sed -nr 's/.*,([0-9]+)]}$/\1/p')

SERVER_JAR="https://papermc.io/api/v2/projects/paper/versions/$MCVER/builds/$LATEST_BUILD/downloads/paper-$MCVER-$LATEST_BUILD.jar"

echo "[Downloading server jar for v$MCVER build #$LATEST_BUILD]"
curl -o ./demo-server/server.jar "$SERVER_JAR"

rm -rf ./.git

git init
git add .
git commit -m "initialized project"

mvn package

rm ./init.sh
