## Synopsis

This is a console utility that identifies tables used in DB2 bind packages. 

## Code Example

Package with maven and then execute either the shell or batch 

```bash
$ {project.home}/target/appassembler/bin/syspack[.bat] -h
```

## Motivation

As my team migrates away from DB2 I needed to find all of the tables and data our programs use and doing that
manually one by one was becoming tedious.  So I created this to make my life a bit easier.

## Installation

This is a maven project and follows all of the conventions of a standard Java project.

## Tests

There are no tests.  That is not common in my code but since this was meant as a shim I didn't follow my own good advice.

## Contributors

Lead Developer: Timothy Storm  - timothystorm@gmail.com

## License

Copyright (c) 2016 Timothy Storm

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.