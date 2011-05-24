#!/bin/bash

#Defina aqui o nome do seu repositório:
REPOSITORIO=antonio

git checkout master
git pull
git checkout $REPOSITORIO
git rebase master

