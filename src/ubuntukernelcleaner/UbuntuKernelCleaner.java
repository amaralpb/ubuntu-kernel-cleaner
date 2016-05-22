/*
 * Copyright (C) 2016 Peter Amaral
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ubuntukernelcleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author peter
 */
public class UbuntuKernelCleaner
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
          {
            ArrayList<String> list;
            list = UbuntuKernelCleaner.getInstalledKernels();
            for (String line : list)
              {
                System.out.println(line);
              }
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("gedit");
          } catch (IOException ex)
          {
            Logger.getLogger(UbuntuKernelCleaner.class.getName()).log(Level.SEVERE, null, ex);
          }

    }

    public String removeInstalledKernels(List<String> kernelsToRemove)
    {
        String executionResult;
        String command[] = new String[kernelsToRemove.size()+3];
        command[0] = "pkexec";
        command[1] ="apt";
        command[2]="purge";
        for(String kernel : kernelsToRemove)
          {
            for (int i = 3; i < kernelsToRemove.size()+3; i++)
              {
                command[i]=kernel;
              }
          }
        try
          {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
          } catch (IOException ex)
          {
            Logger.getLogger(UbuntuKernelCleaner.class.getName()).log(Level.SEVERE, null, ex);
          }
        executionResult = "Kernel(s) Removed!";
        return executionResult;
    }

    /**
     * Get the kernels installed on the machine that are not currently in use
     *
     * @return The installed kernels on the machine excluding the one in use
     */
    public static ArrayList<String> getInstalledKernels()
    {
        ArrayList<String> kernels = new ArrayList<>();
        try
          {
            String[] command =
              {
                "/bin/bash",
                "-c",
                "dpkg -l | tail -n +6 | grep -E 'linux-image-[0-9]+' | grep -Fv $(uname -r) | awk '{ print $2 }'"
              };
            Runtime runtime = Runtime.getRuntime();
            Process process;
            InputStream inputStream;
            BufferedReader reader;

            process = runtime.exec(command);

            process.waitFor();

            inputStream = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null)
              {
                kernels.add(line);
              }

            inputStream.close();
          } catch (IOException | InterruptedException ex)
          {
            Logger.getLogger(UbuntuKernelCleaner.class.getName()).log(Level.SEVERE, null, ex);
          }
        return kernels;
    }

}
