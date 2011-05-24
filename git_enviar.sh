#!/bin/bash

#Defina aqui o nome de seu repositório:
REPOSITORIO=antonio

git checkout master
git pull
git merge $REPOSITORIO
git push

