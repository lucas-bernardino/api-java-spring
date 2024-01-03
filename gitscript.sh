#!/bin/bash
echo "# api-java-spring" >> README.md
git init
git add README.md
git commit -m "first commit"
git branch -M main
git remote add origin https://github.com/lucas-bernardino/api-java-spring.git
git push -u origin main
