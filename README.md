# LogTable
Статичный логер с гибкой настройкой и контролем размера файла
для логирования.

Не статичный логер табличного формата.

## Зависимости
SDK: jbr-17

## ВАЖНО
При подключении логирования необходимо при запуске программы 
задать параметры через метод:<br>
LOG.config(<br>
Папка, в которой будут находиться логи<br>
Имя основного файла для логирования.<br>
Кодировка<br>
Максимальный размер одного файла<br>
Минимальный уровень логирования).