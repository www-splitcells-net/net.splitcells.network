---
title: "There is the bad, there is the horrible, and then there is <sight> error handling."
date: 2021-10-04
author: Mārtiņš Avots
license: EPL-2.0 OR MIT
---
# There is the bad, there is the horrible, and then there is <sight> error handling.
I'm sorry dear reader, but I am emotionally triggered 🙇🏽‍♂️
Maybe this whole article is gibberish and maybe it is not 🤷
I'm truly sorry for the ranty article,
but I need an outlet.
Hopefully, I will not regret this piece.

Every time I think of error handling the following comes to mind:
> NO GOD! PLEASE NO!!! NOOOO!!! NOOOOOO 111!!!!1

I'm missing the old days, when I was young and naive.
Error handling was something, that was done, when it was needed.
It was nothing special, in fact I even enjoyed error handling,
because most of the time I did not need to do anything special.
Just return the error and you are done.

In the background I heard some worrying articles about exceptions,
error codes, panics etc.,
but did not really notice something.
Yeah, there was this strange internship in university,
where exception handling was used extensively for control flow.
It reminded me of the internet pieces warning of this,
but I did not think much of it.
I knew the possibilities,
but I also noticed, that control flows via exceptions seemed to be rare
and most of the time so simple, that it posed no problem.
It was even OKish.

Java started to introduce checked exceptions, and I just ignored it.
Some simple wrapper functions were needed and I was done:
```
try {
    return someWithExceptions(...);
} catch (Exception e) {
    throw new RuntimeException(e);
}
```
Than I saw ***the*** code.
```
try {
    [...]
} catch (Exception e) {}
```
***I saw it over and over and over again***,
and then I noticed it.
The trend of forced error handling, and I lost my consciousness.
When I woke up again, the world was not the same.
It lost some of its colors forever.
That time I lost all hope.
# Example
The programming world is changing and growing.
There will be always some trends, that one dislikes and that is A OKey.
From my point of view, the trend for error handling seems to be strange.
On the surface this trend makes sense, even for me:
errors should not be just ignored, something needs to be done.

This is the good aspect.
No, it is even applaudable.
The horrible aspect for me is the fact,
that developers are forced to write error handling code 😱

Dear reader, you might think, that error handling code is not so bad,
but let me convince you of the opposite.
Let's take the programming language [V](https://vlang.io/).
It is advertised as `Simple, fast, safe, compiled. For developing maintainable software.`.
Right after that, under the section `Safety`,
`Option/Result and mandatory error checks` is listed as a feature.
It is also a link to the error handling documentation.
Under no circumstances it should be possible, that there is bad error handling
code, or is it?
I mean, this is basically their advertisement by example.
It should have some priority, right?

So let's look at the relevant bit of the provided example.
Error handling demonstration from V's [doc](https://github.com/vlang/v/blob/master/doc/docs.md#optionresult-types-and-error-handling):
```
[...]
fn main() {
	repo := Repo{
		users: [User{1, 'Andrew'}, User{2, 'Bob'}, User{10, 'Charles'}]
	}
	user := repo.find_user_by_id(10) or { // Option types must be handled by `or` blocks
		return
	}
	println(user.id) // "10"
	println(user.name) // "Charles"
}
```
> Consider time of writing of this article regarding the current content of V's website.

So, what is being done, when no user is found by the query?
Jep, good old nothing 🤦‍♂️
The language advertises mandatory error checks and the first example does nothing
during an error?

[Wat](https://www.destroyallsoftware.com/talks/wat)

But wait, OHH NOOO, there is more.
Do you notice something else?
The error handling contains a strange return command.
Why is this strange? because V does not have a null value.
That means, that the `return` does not set a value for the user,
but exits the whole function, and I do not see an error code!

Maybe I overlook something?
Maybe the example code returns somehow an error,
if I change `repo.find_user_by_id(10)` to `repo.find_user_by_id(9)`,
und run it locally?

So lets run the original version locally and look at the output:
```
./v run ./test.v 
# 10
# Charles
echo $?
# 0
```
Ok, that's reasonable.
So lets change the argument of `find_user_by_id` in order to test the
error handling code
by searching a non-existent user:
```
./v run ./test.v # Has no output, because no user was found.
echo $?
0
```
So, the return statement exits the main method.
It just converts a mandatory error check to an unchecked error without any
explicit indicator for the caller.
So, let's be clear: the main example for mandatory error checks
removes mandatory error checks?
No, it even **hides** them!

[Wat](https://www.destroyallsoftware.com/talks/wat)

But this does not end here.
Let's look at it again, shall we?
```
[...]
fn main() {
	repo := Repo{
		users: [User{1, 'Andrew'}, User{2, 'Bob'}, User{10, 'Charles'}]
	}
	user := repo.find_user_by_id(10) or { // Option types must be handled by `or` blocks
		return
	}
	println(user.id) // "10"
	println(user.name) // "Charles"
}
```
If you watch closely and project the future behaviour of programmers,
what do we also have here?
We have the basis for control flows similar to the ones possible via exception
handling.
In the or block, which is basically a catch block,
we have full control of the execution.
We can end the method however we like, and we can return any value we like.
The only key ingredient missing,
is the fact, that we know which method returned the error.

Sooner or later there will be a way in order to create a pipe of functions,
that only requires one or block (the following is just my imagination for demonstration purposes):
```
user := repo.process(find_user_by_id(10), get_car_of_user())
    or { // Option types must be handled by `or` blocks
		return
	}
```
# Other Example
So, at the time of writing this article, I thought:
the best way to test my theory, that most explicit error handling is strange,
I thought, let's just google another random example.
Let's talk about Rust.

Ok, so let's have a look: https://doc.rust-lang.org/book/ch09-02-recoverable-errors-with-result.html
```
use std::fs::File;

fn main() {
    let f = File::open("hello.txt");

    let f = match f {
        Ok(file) => file,
        Err(error) => panic!("Problem opening the file: {:?}", error),
    };
}
```
> Please consider the time of writing when comparing to the current content on Rust's website.

So the first example of Rust's documentation book regarding recoverable errors,
creates a panic,
if a file cannot be created and thereby creates an unrecoverable error?

[Wat](https://www.destroyallsoftware.com/talks/wat)

But, wait! The next example must be better, right? RIGHT?? GUYS??

```
use std::fs::File;
use std::io::ErrorKind;

fn main() {
    let f = File::open("hello.txt");

    let f = match f {
        Ok(file) => file,
        Err(error) => match error.kind() {
            ErrorKind::NotFound => match File::create("hello.txt") {
                Ok(fc) => fc,
                Err(e) => panic!("Problem creating the file: {:?}", e),
            },
            other_error => {
                panic!("Problem opening the file: {:?}", other_error)
            }
        },
    };
}
```

Ehh, ok, ok, but there is a third example!
The third is the real one11!!!

```
use std::fs::File;

fn main() {
    let f = File::open("hello.txt").unwrap();
}
```

Ehhh, did I say the third one? ehhh I mean the fourth one!!!

```
use std::fs::File;

fn main() {
    let f = File::open("hello.txt").expect("Failed to open hello.txt");
}
```

But, wait, we at least have no chaining of methods with the error return
type, right guys?
At least not in the same chapter, right?
RIGHT???

```
#![allow(unused)]
fn main() {
use std::fs::File;
use std::io;
use std::io::Read;

fn read_username_from_file() -> Result<String, io::Error> {
    let mut s = String::new();

    File::open("hello.txt")?.read_to_string(&mut s)?;

    Ok(s)
}
}
```

[Wat](https://www.destroyallsoftware.com/talks/wat)

# Error Handling At Splitcells Network
On the top of my hand, I can think of 3 main paradigms I'm using in this project
abart from the normal error handling via result objects.
## Don't handle errors, if possible.
The project has no checked exceptions of its own.
Functions and methods from dependencies including the standard library,
are wrapped by functions/methods converting the checked exceptions to unchecked
ones.
```
try {
    return someWithExceptions(...);
} catch (Exception e) {
    throw new RuntimeException(e);
}
```
## Handle exceptions on process/thread manager level.
The method `net.splitcells.dem.Dem#process` does currently the main exception
handling.
This method is used in order to execute a conceptional process and not a real
one.
It basically sets up an environment starts a thread with a given function
and the constructed environment.
If the thread gets an exception, it is stored.
If the caller is interested in the exception it is also returned.

This works generally very similar to the error handling in the shown
previous examples.
It also demonstrates, that I'm not of the opinion, that exceptions are the real
and only answer to error handling.
Unlike the other examples,
I'm doing exception handling currently with this paradigma only at
a conceptional process level.

Of course such a solution is not suitable for other code bases.
## Create multiple versions of a method with different levels of strictness.
Let's take an example.
The interface `net.splitcells.dem.data.set.Set` defines a value container
in the form of a set.
It extends Java's Set interface for compatibility purposes.
It has 2 methods for adding elements:
* The standard `add` method, throws an exception, if the value is already
  present.
* The method `ensureContains` does not care, if the values is already present.

The usage of `add` is preferred,
because adding an element multiple times without knowing it,
is indicative of a bug.
# Outro
I do not care, which method can throw an error.
I assume that any method can throw an error
and this does not bother me at all.

I hope I was not too harsh on you,
my dear non-existent reader.