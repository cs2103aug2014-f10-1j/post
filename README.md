### STREAM (Simple Task REader And Manager)

This is a project by the awesome group **CS2103AUG2014-F10-1J**, aiming to create a user-friendly to-do-list/task manager, which we call **STREAM**.

Meet the awesome developers of **STREAM**:
* *Wilson Kurniawan*: Team leader, scheduling/tracking/deadlines, integration/versioning; in-charge of ui and stream package
* *John Kevin Tjahjadi*: Documentations and code quality controller; in-charge of model package 
* *Steven Khong Wai How*: Eclipse expert, testing master; in-charge of fileio and logic package
* *Jiang Shenhao*: Compatibility tester; in-charge of parser package

Currently, **STREAM** is capable of basic CRUD (Create, Read, Update, Delete) operations and multiple undo operation, accompanied with user-friendly Graphical User Interface (GUI). A quick user-guide:
* **add** *taskName*: adds a new task
* **delete** *indexNo*: deletes a task
* **clear**: clears all tasks
* **name** *indexNo* *newName*: renames a task
* **modify** *indexNo* *properties and contents*: modifies multiple properties of a task in one go
* **desc** *indexNo* *description*: updates the task's description
* **rank** *indexNo* *newRank*: updates the task's rank
* **start** *indexNo* *startDate*: updates the task's start date
* **due**  *indexNo* *dueDate*: updates the task's due date
* **mark** *indexNo* *(done|ongoing)*: marks a task as either done or ongoing (not done)
* **view** *indexNo*: view the details of a task
* **tag** *indexNo* *tags*: adds tags to a task
* **untag** *indexNo* *tags*: removes tags from a task
* **search** *keyphrase*: searches tasks that match the keyphrase
* **filter** *(criteria)*: filters the tasks that fulfills the criteria
* **sort** *[alphabetical|deadline|starttime|time|importance]* *[ascending|descending]*: sorts tasks according to the category
* **clrsrc**: clears search result
* **first**, **prev**, **next**, **last**: navigates to another page
* **page** *pageNo*: navigates immediately to the chosen page
* **undo**: undoes the last operation
* **help**: gets some help
* **exit**: exits **STREAM**

We support multiple, integrated commands such as

**add** *some task* **-due** *nov 28* **-desc** *buy Pokemon Omega Ruby* **-tag** *#awesome #newgame #whatisfinals* **-rank** *high*

We also support keyboard shortcuts, as follows:
* *a*: **add**
* *d*: **delete**
* *v*: **view**
* *m*: **mark**
* *y*: **modify**
* *t*: **sort**
* *s*: **search**
* *f*: **filter**
* *p*: **page**
* *h*: **help**
* *u*: **undo**
* *e*: **exit**

We aim to add more features and improve this project to even greater heights.

*Cheers,*

*STREAM Developers*
