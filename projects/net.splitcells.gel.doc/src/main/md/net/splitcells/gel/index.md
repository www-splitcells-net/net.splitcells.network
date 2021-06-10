# Introduction to the Generic Allocator

## Introduction for Users

TODO

The Generic Allocator (Gel) is a Java framework for optimization problems.
The framework's primary objective is to provide helpful information regarding a given problem and/or a
solving routine.

It provides tools to model problems simply and understandably,
because without this ability such endeavours become unnecessarily hard and time consuming.
It does this by providing an API, that asks for the required input data step by step in order to build the model.
Simultaneously it gives the information on how to produce such data.

TODO

## Introduction for Developers

The main entry points is located at the class `net.splitcells.gel.Gel`
of the core project `net.splitcells.gel.core`.
Every functionality and public API is reachable via its functions.

The helpers main goal is to provide a decision tree guides developers
by stating what input is required in order to reach a certain goal.
It is highly recommended to prioritize the usage of this class.
This interface helps the programmer to understand the possibilities
and best practices using this framework,
by providing the information,
where it is used.
