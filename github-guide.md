# Github Guide

Never push to main ever!!!!

## Everyday commands
### Before you start working
Make sure that you are working in a repository that is up to date
- `git pull`

And then switch to the branch that you are working in 
- `git switch branch_name`
    - This will also create a new branch if you instead write `git switch -c new_branch_name`
    - If you use this to create a new branch, it will depend on the branch you're currently working in. In most cases when you create a new branch, you want to branch from main.

To check what branch you're in, you can do
- `git checkout`

### When done working
Make sure that you add your files before commiting
- `git add file_name`
- `git add .` (this will add all changed files to your commit)

Commit your added files to the branch you are currently in
- `git commit -m "[type]:[message]"`

You can commit several times before you finally push the commit(s) to Github
- `git push`

If the branch is new, you instead use
- `git push -u origin new_branch_name`

## Conventions
### Commit conventions
We want our commits to reflect the work we've done as effectively as possible, and we will do this by using commit conventions. This means our commit messages will always be in this structure:
- "[type]:[description of work done]"

#### Types
- `fix:` fixes a bug in our codebase
- `feat:` introduces a new feature
- `chore:` cleans up small stuff, for example spelling mistakes in docs
-  `refactor:` refactors either code or folder structure
-  `docs:` introduces or changes documentation, for example licences for API

### Branch conventions
Always create a new branch when starting to work on something new. Every commit in a branch should be to further the goal of that branch. Branches can have the same `types` as commits, however, the structure is slightly different:
- [type]/[branch_name]

## Example: When I work on a new feature

I want to start working on a new feature, so I first need a new branch to work in.
While working I  made a change to the readme file, and I added a new feature in the branch I'm working in.

```
git switch main //Here I make sure that I'm in the main branch to start
git pull //Make sure that my local repo is up to date
git switch -c feat/InfoPage //Here I create a new branch where I will put my new feature

---- write some code and make changes as described ----

git add README.md //Add readme to commit
git commit -m "chore: Rewrote some sentences that were unclear" //Commit the readme file with clear message
git add . //All other files were related to the new feature so I can add them all at the same time
git commit -m "feat: Added an info page where user can read about solar panels" //Commit the changed code files with clear message
git push -u origin feat/InfoPage //Push both commits to the branch
```

After this, if the feature is done, the branch can be merged into main. This can be done with a pull request that requires another or several members to look through the changes. 

## Pull requests
Pull requests are more complicated than other git functions, so here we won't use the terminal. We will only use the green button that says
`Create pull request`. This will open a new pull request, and when it is accepted, the branch can be merged into the main branch.
