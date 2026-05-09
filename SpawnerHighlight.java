name: Build Mod

on:
  push:
    branches: [ main ]
  workflow_dispatch:  # allows you to trigger it manually from GitHub's website

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Download Gradle wrapper
        uses: gradle/actions/setup-gradle@v3

      - name: Build mod jar
        run: ./gradlew build

      - name: Upload jar as artifact
        uses: actions/upload-artifact@v4
        with:
          name: spawnerhighlight-mod
          path: build/libs/spawnerhighlight-*.jar
          if-no-files-found: error
