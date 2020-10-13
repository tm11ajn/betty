import sys

from subprocess import check_output

def generate():
  i_min = 0
  i_max = 999
  file_name_base = "polynondet"

  for i in range(i_min, i_max + 1):
    output_file = open(file_name_base + str(i).zfill(4) + ".rtg", "w")
    output_file.write("q_%d\n" % i)
    for j in range(0, i + 1):
      output_file.write("q_%d -> a\n" % j)
      output_file.write("q_%d -> f(q_%d q_%d) # 1\n" % (j, j, j))
      if j > 0:
        output_file.write("q_%d -> f(q_%d q_%d) # 1\n" % (j-1, j, j-1))
    output_file.close()

generate()