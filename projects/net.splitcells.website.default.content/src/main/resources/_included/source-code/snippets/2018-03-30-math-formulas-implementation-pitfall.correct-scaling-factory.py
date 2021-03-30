def c(self, vector):
	rBase = 0.
	for i in vector:
		rBase += i ** 2
	return (rBase ** (1/2)) ** -2