# AR Library Comps #
Augmented Reality Library by Abha Laddha, Fred Roenn Stensaeth, Josh Pitkofsky, Sabastian Mugazambi, Simon Orlovsky and Thomas Redding
- - - -
# Best Practices #
### I. Committing, Pushing, and Branching ####
1. New branch, always: never push to master.
2. Amend and push often. Diff length: 1 commit.
3. Never push code that breaks functionality.
4. Make sure that all commit messages would be useful if someone else on the team is reviewing the commit history.
5. All nontrivial functions get unit tests.

#### Here are the steps for merging to master ####
1. Ensure your code pass all unit tests.
2. Assign **at least** one other person to review your code on Trello. If you’re pair-programming, the two of you can 3. review the code yourselves. Create a pull request on Github.
4. Merge to master.
5. Run code after merge to ensure it isn’t broken. If it is broken, revert.

### II. Code Review
When reviewing someone’s code, your goal is to make sure it
1. doesn’t have bad side-effects
2. is clear, modular, and designed with future changeability in mind
    * Functions should be short, generalized, and single-purposed
    * Classes make good use of public, protected, and private
3. is well-commented
    * Classes and functions should be prefaced with an overview of their intentions
    * Functions should have their parameters defined
    * ines of code whose purpose is not immediately obvious should have comments
4. Is reasonably efficient (though, premature optimization is the root of all evil)

While code review can seem tedious, it actually saves time in the long-run if done properly by catching issues of these 4 types. To this end, you should be spending about a minute on a review for every ten lines of code. For instance, if I submit a 100 lines of code, you should spend roughly 10 minutes reviewing it. If this seems slow, this rate is actually faster than typical [src]. Please make your diffs as short as possible - people are more likely to review your code if your diff is 50 lines, than if your diff is 500 lines. [src](https://en.wikipedia.org/wiki/Code_review) 

**Note**: everyone should review code regularly w/o having been explicitly asked to do so.

### III. Trello
Trello is the project management tool we will be using to assign and plan tasks.
##### Here are some of the guidelines for working with Trello
If you’re working on a task make sure to assign yourself and move it to in progress
When you’ve completed your task move it to review and assign whoever is going to be reviewing the code.
##### Colors
If you mark a card as red this means that you are requesting for feedback. When requesting for feedback make sure to tag whoever you’re trying to contact (i.e @frederikstensaeth)

##### Approving Card from Review
If the card has an associated PR on Github feel free to just comment on Github and just move the Trello card to approved once it’s done with review
