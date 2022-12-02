#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import unittest
from completeCorrelationMatrixMemories import CompleteCorrelationMatrixMemory
from testingArtifacts import randomVector, randomZeroOneVector, difference, images3x3, randomZeroOnePositiveAndNegativeVector
from random import uniform, sample
class RandomKeyTests(unittest.TestCase):
	def testRandomKeys(self): # TODO This test fails randomly.
		self.__randomKeysTest(9, 9, lambda:randomVector(9))
	def testRandomZeroOneKeys(self): # TODO This test fails randomly.
		self.__randomKeysTest(9, 9, lambda:randomZeroOnePositiveAndNegativeVector(9))
	def __randomKeysTest(self, keySize, valueCount, randomKeyGenerator):
		values = sample(list(images3x3.values()), valueCount)
		mapping = {}
		for i in range(valueCount):
			tmpRandomVector = tuple(randomKeyGenerator())
			while tmpRandomVector in mapping:
				tmpRandomxVector = tuple(randomKeyGenerator())
			mapping[tmpRandomVector] = values[i]
		testSubject = CompleteCorrelationMatrixMemory(mapping)
		overallDifference = 0.
		for key in mapping:
			overallDifference += difference(testSubject.recall(key), mapping[key])
		meanDifference = overallDifference / (9 * 9)
		print("testRandomKeys: meanDifference = " + str(meanDifference)) # TODO make logs instead of console prints
		# Randomly generated vectors are assumed to be not orthogonal.
		# TODO check orthogonality during test and change assertions accordingly
		self.assertTrue(meanDifference < .6)
		self.assertTrue(meanDifference > .4)