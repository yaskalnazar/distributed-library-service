Distributed library service. All the code, except minor fixes, is written using chatCPT 3.5.

Spent time:

    Task 1: 5h 
        (Task 2 was completed first)
        Just basic UI with books Table; And full CRUD REST API for Books entity.
    Task 2: 2h
        Model + Repository + Initial data load by Liquibase.
    Task 3: 4h for basic login + registration
        1h spend for unsuccessful attemp to integrate with Google oauth2.
    Task 4: 3h 
        TODO: The system should keep track of who added or edited the book, as well as the date and time of the change.
        TODO: Validation
    Task 5: 1h
    Task 6: 2h30m 
        Basic solution was added very quickly in about half an hour. Another 2h went into trying to add pagination. From the backend side, the chat gave a solution almost immediately. But still could not offer a working solution for UI. There were constant problems with variables, non-working scripts etc.
    Task 7: start at 3:00 07/04/2023
    Task 9: 3h


Notes:
- Task 3: ChatGPT cannot use latest spring-security 6.0.2 because its base is formed on data until 2021. I had to spend time dealing with deprecated classes.
- Task 3: ChatGPT invented a non-existent GoogleOAuth2UserService and constantly tries to use it. He was unable to write even an approximately working solution for integration with Google.
- Task 4: ChatGPT helped to quickly write forms and methods but often lost or made up variables, so it's worth keeping a close eye on it or always providing a list of variables.
- One of the problems is the limit on the number of characters, because of which HTML pages often cannot be written to the end and break off in the middle.
- Task 9: All tests were written by ChatGPT except for minor corrections. Looks like he can be quite useful in this. But there is a problem that the code of the entire class does not fit in one message, and when you ask him to write tests for methods not yet covered by separate requests, he starts using other approaches, so the tests are inconsistent. 