#!/bin/bash

#Defina aqui o nome de seu repositório:
REPOSITORIO=kepe

git checkout master
git pull
git merge $REPOSITORIO
git push

