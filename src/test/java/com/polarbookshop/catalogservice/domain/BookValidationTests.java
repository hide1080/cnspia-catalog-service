package com.polarbookshop.catalogservice.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookValidationTests {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void whenAllFieldsCorrectThenValidationSucceeds() {
    var book = Book.of("1234567890", "Title", "Autor", 9.90, "Publisher");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenIsbnNotDefinedThenValidationFails() {
    var book = Book.of("", "Title", "Autor", 9.90, "Publisher");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(2);
    List<String> constraintViolationMessage = violations.stream()
      .map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(constraintViolationMessage)
      .contains("The book ISBN must be defined.")
      .contains("The ISBN format must be valid.");
  }

  @Test
  void whenIsbnDefinedButIncorrectThenValidationFails() {
    var book = Book.of("a234567890", "Title", "Autor", 9.90, "Publisher");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
      .isEqualTo("The ISBN format must be valid.");
  }

  @Test
  void whenTitleIsNotDefinedThenValidationFails() {
    var book = Book.of("1234567890", "", "Autor", 9.90, "Publisher");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
      .isEqualTo("The book title must be defined.");
  }

  @Test
  void whenAuthorIsNotDefinedThenValidationFails() {
    var book = Book.of("1234567890", "Title", "", 9.90, "Publisher");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
      .isEqualTo("The book author must be defined.");
  }

  @Test
  void whenPriceIsNotDefinedThenValidationFails() {
    var book = Book.of("1234567890", "Title", "Author", null, "Publisher");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
      .isEqualTo("The book price must be defined.");
  }

  @Test
  void whenPriceDefinedButZeroThenValidationFails() {
    var book = Book.of("1234567890", "Title", "Author", 0.0, "Publisher");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
      .isEqualTo("The book price must be greater than zero.");
  }

  @Test
  void whenPriceDefinedButNegativeThenValidationFails() {
    var book = Book.of("1234567890", "Title", "Author", -9.90, "Publisher");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
      .isEqualTo("The book price must be greater than zero.");
  }}
