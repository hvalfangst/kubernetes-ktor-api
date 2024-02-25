package model

class EmailAlreadyRegisteredException(message: String) : RuntimeException(message)
class InvalidEmailException(message: String) : RuntimeException(message)
class InvalidDateOfBirthException(message: String) : RuntimeException(message)