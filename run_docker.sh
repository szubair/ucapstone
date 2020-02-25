#!/usr/bin/env bash

image_name='gcproj6'
proj_name='xxxxxxxxxxxxxxx'
#Build image and add descriptive tag
echo "image name: $image_name"
docker build -t $image_name . 

# List docker images
echo "#########"
sleep 2 
docker image ls

# Run flask app in detach mode
echo "#########"
sleep 2
docker run --rm $image_name $proj_name
