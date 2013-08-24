package br.ufpe.cin.dsoa.management.shell;

import java.util.List;

import org.apache.felix.shell.Command;

public interface DsoaCommand extends Command{

	List<String> getParameters();

}