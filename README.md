## Minimal Game Framework

The *"Minimal Game Framework"* (or short *Mgf* with an uppercase "M") as the name suggests, is a very small and minimal framework to build 2D-Arcade-Games from Pong till Pac-Man.

The framework is build on the standard library of "Java SE 6" and should run under both the JDK/JRE and the OpenJDK/IcedTea. No other libraries are used.

It can be used to create small to medium sized games and uses the Swing window-toolkit for the visual representation.

### Game List

A small list of games which where created using *Mgf*:

* [Pacman](https://bitbucket.org/LukasKnuth/pacman)

If you created a game yourself and it is a public, OpenSourced project, you can add your game (with link) to this list and send me a pull-request.

### Implementation Repository

Since the goal of this framework is to be minimal (only offer very basic functionality), things are kept as abstract as possible.

Since actual implementations of the offered interfaces should not go into the framework itself, we have (or will in the future) provide a kind of "repository" for example implementations and other helpful examples which might be usable in your project, too.

Examples for such implementations include:

* A basic "figure"-class, implementing the standard listeners and default functions (like "getWidth()")
* CollisionTest implementations for round/square/complex objects

## Contributors

* Lukas Knuth (Main Developer)
* Fabian Bottler (Contributor)

To contribute to this project, just fork the project here on GitHub, do your work offline, commit to your copy and send me a pull-request. More information on that are available [here](http://git-scm.com/book/en/Distributed-Git-Contributing-to-a-Project).

## To Do

* Tutorials on certain aspects (like collision detection, square and circle)
    * Five groups: Basic Training, Graphics, Sound, Physics, Gameplay

### Implementation

* Add a configuration-system to easily load and store (file) the config. Enable engine-configuration through these as well.
    * Enable Anti-Aliasing in three quality steps (None, Normal, High)
    * Add a "Debugging" mode (draw a grid on-screen, an FPS counter, log like World of Goo)
    * Fullscreen for the game-window.

* Re-implement objects in the game:
    * All objects extend "GameObject"-class (contains position, rotation, scaling. Make ALL THESE float/double!!!). Has all methods (render, collision, update) and uses overwriting. Deprecate the interfaces and only add the objects.
    * Hitbox class to simplify collision-detection between objects (make part of GameObject??). Allow: obj1.collides(obj2)

* Replace most Interfaces with classes, since interfaces can't be extended without breaking old code.