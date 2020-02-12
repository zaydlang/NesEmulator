# Personal Project: NES Emulator

## Overview

This program will emulate the 6502 NES CPU instructions (not including any illegal opcodes, as few games use those), as well as the PPU for graphics and APU for sound. It will allow you to feed it a ROM and it will attempt to read it and emulate the NES. While there are many ROM memory Mappers (a total of 49), the most common ones will be implemented as to allow this emulator to run most games. The program can give you a detailed description of each instruction the CPU executes as it executes them, and the states of the registers and RAM before and after each instruction. 

Planned features include:
- Full CPU emulation
- PPU emulation to allow display
- APU emulation for sound
- At the very least, implementation of the NROM memory Mapper, as it is the most widely used.

Features that I hope to get to:
- Savestates that allow you to copy the CPU's state at a specific instruction and return to it at will.
- More Memory Mappers
- An API that gives the user read/write access to the RAM state during emulation, which allows them to write their own Java class that runs concurrently with the emulator.

## User Stories

- As a user, I want to be able to accurately run the legal opcode instructions on this emulator.
- As a user, I want to be able to run a ROM that uses the NROM memory mapper.
- As a user, I want to be able the Addressing Modes to read memory properly.
- As a user, I want the opcodes to be able to edit the registers and memory properly.
- As a user, I want to be able to see the effects of each instruction on the CPU (what registers have been modified, how many cycles the CPU has gone through, etc.)
- As a user, I want to be able to set breakpoints at a specific program counter.
