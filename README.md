# mirotest
Implementation for Miro backend developer test task (see [Test task](Take_Home_Test_(Java)_Miro_092020.pdf)).

The basic task is implemented as well as "Complication nr 2" (Filtering).

## Basic Widget REST endpoints

- Get a list of widgets in ascending z order: **GET /widgets**

- Create a new widget: **POST /widgets**


- Get a widget by id: **GET /widgets/{id}**


- Update a widget by id: **PUT /widgets/{id}**


- Delete a widget by id: **DELETE /widgets/{id}**

## Additional Widget REST endpoint - Complication 2

**POST /widgets/search**   
Gets a list of widgets that are contained in a bounding box (in ascending z order)

## API Objects
REST request bodies may contain the following JSON objects:

- com.miro.model.Widget
- com.miro.model.SearchBounds (only for **/widgets/search**)




