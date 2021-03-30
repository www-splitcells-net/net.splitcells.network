#!/usr/bin/env python3
"""This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at http://mozilla.org/MPL/2.0/.

Based on the paper "Correlation Matrix Memories" by TEUVO KOHONEN.
"""
__author__ = 'Mārtiņš Avots'
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