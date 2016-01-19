# Research software #

This software was written while creating research paper `Software metrics in Boa large-scale software mining infrastructure: challenges and solutions`.

Authors:
Agnieszka Patalas, Michał Malinka, Wojciech Cichowski, Piotr Maćkowiak

under guidance of associate professor Lech Madeyski

Models and Metrics in Software Engineering Project

Wrocław University of Technology

<br>
To allow easy management of Boa jobs and connecting job outputs with creating defect prediction models, a simple Java program has been written.
The software uses [Boa Java API](https://github.com/boalang/api) to run jobs.

Few simple documents (input files) should be used to configurate query. They are stored in `res/` directory:

* config.properties - stores the login data for Boa account. The account is used for running Boa jobs through API. Both `user` and `pass` attributes must be real and belong to registered Boa account. They are mandatory. Not providing those attributes will result in program failure.

* input.xml - keeps the configuration for the program:

* * `<metrics>`: a list of metrics as a list of `<metric>` attributes; each metric should be stored in a `*.boa` file with the same name and placed in `metrics/` dictionary. `*.boa` files must have only one output named `m`
* * `<dataset>`: the ID of the picked dataset; the IDs of working datasets are defined in `datasets_dictionary.xml` file;
* * `<output_parameter>`: defines the evaluating value for prediction model; so far, only the fixing revision attribute is used in such manner. It can be imported in the same way as `<metric>`
* * `<result_file_path>`: prediction output file name, relative to main directory

<br>
Program builds simple prediction model which can be used to predcit number of fixing revision in no-classifed data set.


Result file has following structure:

Columns: List of input metrics, eg. WMC, CNOF, RFC

Rows: Values of each metic

Last column named "fixingRevisions" contains value calculated by predicition model.
