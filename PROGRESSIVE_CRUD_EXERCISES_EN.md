# 5 Progressive CRUD Exercises with Spring

These exercises help you practice CRUD operations: Create, Read, Update, and Delete. They are ordered from easiest to more challenging.

For each exercise, the goal is to build the requested behavior and check that it works. You are free to choose the organization and storage tools; no repository is required.

## Exercise 1 — A very simple to-do list

### Goal

Create a small API for managing to-do tasks.

### Task data

Each task contains:

- a unique identifier;
- a title;
- an indication of whether the task is complete.

### What to build

- Add a task with a title.
- Display all tasks.
- Display a task by its identifier.
- Change a task's title or completion status.
- Delete a task by its identifier.

### Success criteria

- A new task receives an identifier.
- An unknown task must not be updated or deleted as if it existed.
- After an update, reading the task shows its new values.
- After deletion, the task no longer appears in the list.
- A missing or empty title is rejected.

## Exercise 2 — A contact book

### Goal

Manage contacts and make it possible to find a person easily.

### Contact data

Each contact contains:

- a unique identifier;
- a first name;
- a last name;
- an email address;
- an optional phone number.

### What to build

- Create a contact.
- Display all contacts.
- Display a contact by its identifier.
- Search contacts by first or last name, without considering uppercase and lowercase letters.
- Update a contact's information.
- Delete a contact.

### Success criteria

- First name, last name, and email address are required.
- The email address must have a valid format.
- A search with no matches returns an empty list.
- The search works even if the letter casing does not match exactly.
- Updating a contact must not create a second contact.

## Exercise 3 — A book catalog

### Goal

Create a catalog that can be browsed and filtered.

### Book data

Each book contains:

- a unique identifier;
- a title;
- an author;
- a publication year;
- a genre;
- an indication of availability.

### What to build

- Add a book.
- Display all books.
- Display a book by its identifier.
- Filter books by author, genre, or availability.
- Update a book's information.
- Delete a book.

### Success criteria

- The title and author are required.
- The publication year must be plausible and cannot be in the future.
- Multiple filters can be used together.
- A missing filter must not prevent other matching results from being displayed.
- A search with no matches returns an empty list.

## Exercise 4 — A small product catalog

### Goal

Manage a shop's products and provide several search criteria.

### Product data

Each product contains:

- a unique identifier;
- a name;
- an optional description;
- a price;
- a category;
- a quantity in stock;
- a creation date.

### What to build

- Create a product.
- Display all products.
- Display a product by its identifier.
- Search products by name or category.
- Display products whose prices fall within a given range.
- Update a product's information.
- Delete a product.

### Success criteria

- Name, price, and category are required.
- The price cannot be negative.
- The stock quantity cannot be negative.
- The creation date is set automatically when the product is created.
- The client does not choose the identifier or creation date.
- During an update, the identifier and creation date remain those of the existing product.
- Name, category, and price filters can be combined.

## Exercise 5 — Room reservation management

### Goal

Manage rooms and their reservations while following availability rules.

### Room data

Each room contains:

- a unique identifier;
- a name;
- a maximum capacity;
- an indication of whether it can be booked.

### Reservation data

Each reservation contains:

- a unique identifier;
- the room's identifier;
- the name of the person making the reservation;
- a start date and time;
- an end date and time;
- the number of participants.

### What to build

- Create, view, update, and delete rooms.
- Create a reservation for a room.
- Display a reservation by its identifier.
- Display the reservations for a room.
- Display reservations within a given time period.
- Update or cancel a reservation.

### Success criteria

- A reservation must be for an existing room that can be booked.
- The number of participants must be positive and cannot exceed the room's capacity.
- The end time must be after the start time.
- Two reservations for the same room cannot overlap.
- Reservations for different rooms can take place at the same time.
- An updated reservation must also follow all availability rules.
- An unknown reservation or room is reported clearly.
- Deleting a room that has reservations must follow one consistent rule chosen for the exercise: reject the deletion or also delete its reservations.

## Suggestions for checking each exercise

Regardless of the approach you choose, check at least these situations:

- successful creation;
- reading an existing item;
- reading an unknown item;
- successful update;
- successful deletion;
- invalid data;
- an empty list or a search with no matches;
- confirming that changes appear in later reads.

You can start with Exercise 1 and move to the next one when all its success criteria are met.
