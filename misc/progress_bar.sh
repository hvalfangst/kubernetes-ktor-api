#!/bin/sh

bar_length=50
bar_char="="
sleep_time=$1

for i in $(seq 1 10); do
  progress=$((i * 10))
  progress_bar=""
  for j in $(seq 1 $((bar_length * i / 10))); do
    progress_bar="$progress_bar$bar_char"
  done
  spaces=$((bar_length - (bar_length * i / 10)))
  for k in $(seq 1 $spaces); do
    progress_bar="$progress_bar "
  done
  echo -ne "\r[$progress_bar] $progress %"
  sleep $sleep_time
done