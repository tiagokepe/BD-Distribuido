#!/bin/bash

#Defina aqui o nome do seu repositório:
REPOSITORIO=kepe

git checkout master
git pull
git checkout $REPOSITORIO
git rebase master

