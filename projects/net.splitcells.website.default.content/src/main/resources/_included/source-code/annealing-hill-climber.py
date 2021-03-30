#!/usr/bin/env python3
"""This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at http://mozilla.org/MPL/2.0/."""
__author__ = 'Mārtiņš Avots'
import unittest
from random import uniform, shuffle
from math import log, inf
class annealing_relative_rating:
    def __init__(self,
                 rating_function,
                 temperature_function,
                 rating_improvement_factor,
                 rating_diminishment_factor):
        self.rating_function = rating_function
        self.temperature_function = temperature_function
        self.counter = 0
        self.rating_improvement_factor = rating_improvement_factor
        self.rating_diminishment_factor = rating_diminishment_factor
    def __call__(self, solution):
        rating = self.rating_function(solution)
        self.counter += 1
        temperature = self.temperature_function(self.counter)
        if rating == 0 or temperature == 0:
            return rating
        elif uniform(0, 1) >= temperature:
            return rating * self.rating_improvement_factor
        else:
            if rating * self.rating_diminishment_factor == 0:
                return rating
            else:
                return rating * self.rating_diminishment_factor
class annealing_absolute_rating:
    def __init__(self, rating_function, temperature_function, max_rating):
        self.rating_function = rating_function
        self.temperature_function = temperature_function
        self.counter = 0
        self.max_rating = max_rating
    def __call__(self, solution):
        rating = self.rating_function(solution)
        self.counter += 1
        temperature = self.temperature_function(self.counter)
        if rating == 0 or temperature == 0:
            return rating
        elif uniform(0, 1) >= temperature:
            return rating
        else:
            return self.max_rating
def climb_hill(rating_function, problem_space, current_solution):
    best_solution = current_solution
    for neighbour in problem_space(current_solution):
        if rating_function(best_solution) > rating_function(neighbour):
            best_solution = neighbour
    return best_solution
def Queen_N_problem(n):
    return  [0] * n
def Queen_N_rating(queen_problem):
    rVal = 0
    for i in range(len(queen_problem)):
        for j in range(len(queen_problem)):
            if [i] != [j] and queen_problem[i] == queen_problem[j]:
                rVal += 1
    for i in range(0, len(queen_problem) - 1):
        for j in range(i + 1, len(queen_problem)):
            k = j - i
            if queen_problem[i] == queen_problem[j] - k:
                rVal += 1
            if queen_problem[i] == queen_problem[j] + k:
                rVal += 1
    return rVal
def Queen_N_neighbours(queen_problem):
    rVal = []
    for i in range(len(queen_problem)):
        for j in range(len(queen_problem)):
            neighbour = list(queen_problem)
            neighbour[i] = j
            rVal.append(neighbour)
    shuffle(rVal)
    return rVal
class HillClimbingTestLocalOptima(unittest.TestCase):
    """By default only functions starting with test are executed during testing."""
    def __temperatureTestFunction(self, x):
        """Determined by trial and error from universal allocation program's temperature functions."""
        return (log(4.0) / log((x + 1)**1.5)) - 0.05
    def __hillClimbingTest_GetsStuckInBadLocalOptima(self, rating_function, max_iterations):
        for i in range(max_iterations):
            solution = Queen_N_problem(8)
            for i in range(1000):
                solution = climb_hill(rating_function, Queen_N_neighbours, solution)
                if Queen_N_rating(solution) == 0:
                    break
            if Queen_N_rating(solution) != 0:
                return True
        return False
    def testClassicHillClimbingHasBadLocalOptima(self):
        self.assertTrue(
            self.__hillClimbingTest_GetsStuckInBadLocalOptima(Queen_N_rating, 10)
            )
    def testAbsoluteAnnealingHillClimberFindsGlobalOptima(self):
        self.assertFalse(
            self.__hillClimbingTest_GetsStuckInBadLocalOptima(
                annealing_absolute_rating(
                    Queen_N_rating,
                    self.__temperatureTestFunction,
                    inf), # A floating-point positive infinity.
                10)
        )
    def testRelativeAnnealingHillClimberFindsGlobalOptima(self):
        self.assertFalse(
            self.__hillClimbingTest_GetsStuckInBadLocalOptima(
                annealing_relative_rating(
                    Queen_N_rating,
                    self.__temperatureTestFunction,
                    1./8., # Determined by trial and error.
                    8), # Determined by trial and error.
                10)
        )
if __name__ == "__main__":
    unittest.main()