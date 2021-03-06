# kickass-plugin 
Eclipse plugin for convenient Kick Assembling binaries for the Commodore 64

    Copyright (C) 2012 P-a Backstrom under General Public License (v2).
   
    Based on the project ASM Plugin, http://sourceforge.net/projects/asmplugin
    by Andy Reek, Daniel Mitte
    Copyright (C) 2006 Andy Reek, Daniel Mitte under General Public License (v2).

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

## Features
* Syntax-highlighting of 6502-opcodes and KickAssembler keywords and functions
* Auto-completion of labels, functions, macros, constants etc
* Outline support. The outline shows segments and labels for quick access.
* Automatic building of source-files
* Launch support in VICE. Run/Debug your .prg in VICE with labels and breakpoints.

## Requirements
* For build support you will need the latest version of the Kick Assembler jar. [Kick Assembler](http://theweb.dk/KickAssembler/)
* VICE for running/debugging [VICE](http://vice-emu.sourceforge.net/)

## Installation
Either download the zip-file from github [Downloads](https://github.com/p-a/kickass-plugin/downloads)
Or clone the project from Github, and build and export in Eclipse.
The resulting jar goes into the dropins-directory in your Eclipse install.

## Usage
Install plugin (see above). Create new Kick Assembler project.

### Configuration
Go to Preferences/KickAssembler.
In 'Compiler' you must specify the path to Kickass.jar if you want any compilation to occur.
Here you can also choose what flags and arguments to provide Kickass with at compilation time.
The 'Debugger'-page holds the path to the executable X64 (VICE).  

You can also specify global library directories at the compiler-page.
Project-specific library directories are configured at the project level, easiest reached by right-clicking
the project-folder, and choose `Build Path->Configure Build Path`.
Here, you can configure additional source and library directories.
Only the directories specified as source will be subject for code-completion.
All libdirs are also included on Kickass's classpath. So if you have a Kickassplugin, class or jar, make sure its
containing directory is specified as a libdir.

### Write code
Create `.asm` or `.s` files.
Include files are to be named with the extension `.inc` if you want them to be detected by the plugin.
If an include filed is modified, a full build is triggered.
If an ordinary source file is modified, only this file is compiled. However, this usually means a full compile anyway.

### Run or debug cool demo
CTRL-F11 (CMD-F11) runs the compiled binary in VICE. Shift-CTRL-F11 starts VICE with breakpoints attached (debug-mode)

### Debugging with breakpoints
Labels named `breakpoint` or `breakpoint[1..9]` will trigger a break in VICE if launched as Debug.
Of course there is always the option to provide your own scheme since you can specify commandline arguments to VICE. 


## Known bugs
Many. Will accept pull requests.

## Disclaimer
Please note that this project is not operated by, sponsored by, endorsed by, or affiliated with neither Kick Assembler nor VICE in any way.

  
