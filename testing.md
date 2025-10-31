## GitHub Link: https://github.com/TurnerCorban/Refactoring-Project

## Test Plan:

| Test ID | Level     | Input                                                                                                                                                            | Expected Output <td>Result</td>                                                                  |
|---------|-----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| UT-01   | Unit      | ValidationUtil.validateBannerId (“B001”) <br/>ValidationUtil.validateEmail(“test@email.com”)<br/>ValidationUtil.validateCourseCode(“CSCI4430”)                   | No Exception Thrown <td style="background-color:green;color:white;">PASS</td>                    |
| UT-02   | Unit      | ValidationUtil.validateBannerId("InvalidID") <br/> ValidationUtil.validateEmail("invalid-email") <br/>    ValidationUtil.validateCourseCode("InvalidCourseCode") | IllegalArgumentException thrown <td style="background-color:green;color:white;">PASS</td>        |
| CT-03   | Component | service.addStudent(“B005”, “Test Student”, “test@uca.edu”)                                                                                                       | Student object is created and saved<td style="background-color:green;color:white;">PASS</td>     |
| CT-04   | Component | Add student Jonny (B008), add student Blane (B009), enroll Jonny and Blane into class (CSCI4453) with capacity 1.                                                | Student B009 has waitlist position 1<td style="background-color:green;color:white;">PASS</td>    |
| ST-05   | System    | Add student B020, add course FLOW1001, enroll student B020 in CSCIFLOW1001                                                                                       | Student B020 shows enrolled in FLOW1001<td style="background-color:green;color:white;">PASS</td> |

## Execute tests
```bash

mvn test

```