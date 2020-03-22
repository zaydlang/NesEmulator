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
- As a user, I want to the opcodes to be able to edit the registers and memory properly.
- As a user, I want to be able to see the effects of each instruction on the CPU (what registers have been modified, how many cycles the CPU has gone through, etc.)
- As a user, I want to be able to set breakpoints at a specific program counter.
- As a user, I want to be able to view the nametables, pattern tables, and OAM of the NES.
- As a user, I want to be able to render 8x8 sprites and backgrounds onto the screen.

## How to Use
The File menu contains options such as Open, Save State, and Load State. 
- Open allows you to open any NES ROM that uses the NROM memory mapper (one popular one that is provided in this project is Donkey Kong).
- Save State allows you to save the NES' state into a file stored in ./data/save/savestate.sav
- Load State allows you to load the NES' state from the above file.
The View menu allows you to view parts of the CPU and PPU
- CPUViewer and BreakpointViewer only work properly if the emulator is paused! This is where the "add X to Y" requirement is fulfilled; you can add a breakpoint to a list of breakpoints, and cycle into a breakpoint.
- The rest of the viewers allow you to see the PPU's state. The Pattern Tables show you the tileset, the Nametables show you their arrangement on the screen, and the OAM shows you the current sprites! Press spacebar on the pattern table viewer or nametable viewer to change the color palette.

Note that controls do not yet work; this emulator simply displays the NES onto the screen.

(To the TA): Because I didn't get to give you a proper goodbye, I'd like to let you know you were really nice and helpful, and I enjoyed my time in the lab. Thanks for being our TA!