package file.splitter.dict;


public class Constants {
    public static final String APPLICATION_TITLE = "Программа поиска и разделения байт по файлам по заданному ключу";
    public static final String BYTE = " Байт";
    public static final String LOGGING_TITLE = "Текущий статус:";
    public static final String CHOOSE_RESULT_FOLDER = "Выберите папку для сохранения файла с результатами";
    public static final String ERROR_RESULT_FOLDER_NOT_SET = "Папка для сохранения результатов не выбрана.";
    public static final String ERROR_TITLE = "Произошла ошибка!  ";
    public static final String ERROR_HEADER = "Ошибка!";
    public static final String BEGIN_CONVERTATION = "Начать поиск байтов";
    public static final String CANCEL_BUTTON = "Отменить";
    public static final String CLEAN_LOGGER = "Очистить область сообщений";
    public static final String CHOOSE_FILE = "Выбор базового файла";
    public static final String ERROR_NO_FILE = "Файл не выбран или не существует!";
    public static final String REG_EX_VALUE = "Введите значение разделителя (вводите внимательно правильные значения в байтах):";
    public static final String ERROR_REGEX_FIELD_EMPTY = "Отсутствует значение RegEX в поле ввода!";
    public static final String ERROR_REGEX_FIELD_LENGTH_SMALL = "Поле значения для поиска должно содержать минимум 2 символа.";
    public static final String LOGGER_ENTERED_BYTE_ARRAY = "\n Введенный массив байт - ";
    public static final String LOGGER_FILE_SIZE = "\n Общий размер файла - ";
    public static final String LOGGER_FILE_SPLITS_COUNT = "\n Количество промежуточных шагов - ";
    public static final String LOGGER_OUTPUT_IS_TO_BIG = "\n Количество найденных совпадений слишком большое для вывода в консоль. " +
            "\n Проверьте файл с результатами в папке указанной вами ранее. " +
            "\n Количество найденных елементов = ";
    public static final String LOGGER_SAVE_RESULT = "\n Результаты проверки сохранены в файл. Найдено - ";

    public static final String LOGGER_FOUNDED_VALUES_COUNT = "\n Количество найденных совпадений - ";
    public static final String LOGGER_SEARCH_IS_OVER = "\n Поиск завершен.";
    public static final String LOGGER_SPLITTING_IS_OVER = "\n Разделение файла завершено.";
    public static final String LOGGER_NO_EQUALITY_FOUND = "\n Совпадений не выявлено.";
    public static final String LOGGER_FILE_CHECK_BEGIN = "\n Начинается анализ выбранного файла.";
    public static final String LOGGER_FILE_SPLIT_BEGIN = "\n Начинается разделение выбранного файла на части.";
    public static final String LOGGER_FILE_SPLIT_PART = "\n Разделение части файла. Ожидайте... (время завершения зависит от величины файла)";
    public static final String LOGGER_FILE_CHECK_PROCEED = "\n Проводится проверка части файла #";
    public static final String LOGGER_EQUALITY_FOUND_FIRST_INDEX = "\n Совпадение по ключу - Индекс начала: ";
    public static final String LOGGER_EQUALITY_FOUND_LAST_INDEX = ";  Индекс конца: ";
    public static final String CHECK_DATE = "\n Дата проверки: ";
    public static final String CHECKED_FILE = "  Проверенный файл: ";
    public static final String FIRST_INDEX = "\n Первый индекс - ";
    public static final String INDEX_OF_THE_END = "  Последний индекс - ";
    public static final String CHECK_BOX_STRICT_INPUT = "Использовать 16-тиричное(hex) представление";
    public static final String CHECK_BOX_STRICT_INCORRECT_LENGTH = "Неверное колличество введенных значений. При строгом вводе строка байтов должна быть четной. Каждый байт имеет 2 символа.)";
    public static final String CHECK_BOX_SPLIT = "Производить разделение файлов после поиска";
}
