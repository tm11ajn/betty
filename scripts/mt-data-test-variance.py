import sys
import re
import time

from subprocess import check_output

def mt_data_test_variance():
  file_base = "../../../../MT_data/unzipped/2015/"
  file_type = "rtg"
  start_number = 0
  end_number = 926
  N_min = 250
  N_max = 10000
  N_jump_by = 250
  number_of_repetitions = 5
  threshold = 0.01 # Max tolerable value for variance/mean

  for N in range(N_min, N_max + 1, N_jump_by):
    output_file_name = "result_N_%d" % (N) + ".txt"
    output_file = open(output_file_name, "w")

    for file_number in range(start_number, end_number + 1):
      file_number_string = str(file_number).zfill(4) 
      file_name = file_base + file_number_string + "/data"
      total_time = 0
      variance = 0
      mean = 0
      total_number_of_repetitions = 0
      
      while variance >= threshold * mean:
        temp_result = []
        total_temp_time = 0

        for repetition in range(1, number_of_repetitions + 1):
          #start_time = time.time()
          output = check_output(["java", "-Xmx16g", "-Xms12g", "-jar", "../../../nbesttrees/BestTrees_v.2.9.jar", "-N", "%d" % N, "-f", file_name, "-t", file_type, "-timer"])
          #temp_time = time.time() - start_time

          last_string = (output.splitlines())[-1]
          print(last_string)
          match = re.search(r'\d+.?\d*' , last_string)
        
          if match:
            temp_time = float(match.group()) / 1000
          else:
            sys.stderr.write("Could not find running time in output file \n")
            sys.exit(1)

          temp_result.append(temp_time)
          total_temp_time = total_temp_time + temp_time
          total_number_of_repetitions = total_number_of_repetitions + 1

        mean = total_temp_time / number_of_repetitions
        temp_variance = 0

        for time in temp_result:
          temp_variance = temp_variance + (time - mean) ** 2

        temp_variance = temp_variance / (number_of_repetitions - 1)
        #print("temp_variance: %s \n temp_mean: %s \n " % (temp_variance, mean))
        variance = (variance + temp_variance) / 2
        total_time = total_time + total_temp_time
        mean = total_time / total_number_of_repetitions
        #print("variance: %s \n mean: %s \n " % (variance, mean))
      
      total_time = total_time / float(total_number_of_repetitions) * 1000
      output_file.write("%-s  %.3f\n" % (file_number_string, total_time))
      print("Done with run for N=%d and file=%s (took %s milliseconds and %d repetitions)" % (N, file_name, total_time, total_number_of_repetitions))

    output_file.close()

if len(sys.argv) > 1:
  sys.stderr.write("Usage: python mt_data_test_variance.py\n")

mt_data_test_variance()