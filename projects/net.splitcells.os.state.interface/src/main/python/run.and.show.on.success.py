#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import subprocess
import sys
from threading import Thread
from time import time
isRunning = True
output=list()

# TODO Print stderr via "echo.error".

def readAndOutput(stream):
	lastEchoTime = -1
	with stream:
		for line in iter(stream.readline, b''):
			if isRunning:
				global output
				lineStr = line.decode(sys.stdout.encoding)
				output.append(lineStr)
				currentEchoTime = time()
				if currentEchoTime - lastEchoTime > 3:
					lastEchoTime = currentEchoTime
					subprocess.call(['echo.line.current.set', lineStr])
			else:
				return
if __name__ == '__main__':
	if not len(sys.argv) == 2:
		print('Please quote commands that need to be executed. Arguments that were provided: ' + ' '.join(sys.argv[1:]))
		sys.exit(1)
	subprocess.call(['echo.filtered', 'Executing \"' + sys.argv[1] + '\".'])
	process = subprocess.Popen(sys.argv[1],
		shell = True,
		bufsize = 100000,
		stdout = subprocess.PIPE,
		stderr = subprocess.PIPE)
	#
	
	outputThread = Thread(target=readAndOutput, args=[process.stdout])
	errorThread = Thread(target=readAndOutput, args=[process.stderr])
	outputThread.start()
	errorThread.start()
	#
	exitCode=process.wait()
	
	#
	if exitCode != 0:
		isRunning = False
		outputThread.join()
		errorThread.join()
	else:
		subprocess.run(['echo.line.current.clear'])
		outputThread.join()
		errorThread.join()
		for line in output:
			print(line, end = '')
	subprocess.run(['echo.line.current.clear'])
	sys.exit(exitCode)