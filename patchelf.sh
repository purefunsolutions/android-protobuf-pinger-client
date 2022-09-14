#!/usr/bin/env bash
nix-shell -p patchelf --run "patchelf --set-interpreter /nix/store/fz54faknl123dimzz6jsppw193lx2mip-glibc-2.35-163/lib/ld-linux-x86-64.so.2 ${HOME}/.gradle/caches/modules-2/files-2.1/io.grpc/protoc-gen-grpc-java/1.46.0/a894dd06ea3aa402406e836b9fa026ab58a3982e/protoc-gen-grpc-java-1.46.0-linux-x86_64.exe"
