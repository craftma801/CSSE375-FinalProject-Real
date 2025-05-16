#!/bin/sh
mkdir Pandemic
cp target/*.jar Pandemic
cp -r resources Pandemic/
ls -l Pandemic
zip -r pandemic.zip Pandemic/*
mkdir artifacts
mv pandemic.zip artifacts
