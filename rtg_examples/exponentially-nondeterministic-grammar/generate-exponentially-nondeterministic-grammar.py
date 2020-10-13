import sys

from subprocess import check_output

def generate():
  i_min = 0
  i_max = 499
  file_name_base = "expnondet"

  for i in range(i_min, i_max + 1):
    output_file = open(file_name_base + str(i).zfill(4) + ".rtg", "w")
    output_file.write("q_f\n")
    for j in range(0, i + 1):
      output_file.write("q_f -> q_%d\n" % j)
      output_file.write("q_%d -> a\n" % j)
      for k in range(0, i + 1):
        output_file.write("q_%d -> f(q_%d q_%d) # 1\n" % (j, j, k))
        if j != k:
          output_file.write("q_%d -> f(q_%d q_%d) # 1\n" % (j, k, j))
    output_file.close()

generate()