package sully.vc.simpletype_rpg.parser;

import static core.Script.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import sully.vc.simpletype_rpg.Cast;
import sully.vc.simpletype_rpg.Data;
import sully.vc.simpletype_rpg.Skill;
import sully.vc.simpletype_rpg.Item;

import static sully.vc.util.Error_handler.*;
import static sully.vc.simpletype_rpg.parser.FlagsVH.*;
import static sully.vc.simpletype_rpg.parser.Data_front.*;

import static sully.vc.simpletype_rpg.Data.*;


// data_load.vc for Sully www.verge-rpg.com
// Zip 25/08/2004
//
//         --------------
//         Functions
//         --------------
//
// Split into three types in the file, in order:
//     helper functions:
//         Type comparisons and conversions that throw errors on suspicious results
//     DumpStruct functions:
//         log() the data contained within the structures set out in data.vc
//     LoadStruct functions:
//         Loads data from file into the structures set out in data.vc
// Within each section the functions are arranged alphabetically
//
//         ------------------------------
//         Note on coding style
//         ------------------------------
//
// All this code is written in a very procedural, c kind of style, with rather too much copy/paste
// This is in general rather bad practice, as it tends to make everything harder to update or adapt
// There are a few reasons for this however:
//     -File i/o is by nature linear, and implies a similar coding style
//     -With no generic access to structures, each needs their own load function, even with similar components
//     -Even where a specific check looks to be duplicated across functions, there are differing error detection requirements
// So the code currently does all it has to, but a future improvement would certainly be to make a more generic load function
//
//         ----------------------
//         Note on format
//         ----------------------
//
// I use mostly standard naming conventions: ALL_CAPS defines, CamelCase() functions, and lower_case variables
// However due to similar variables across functions I have prefixed all variables based on function name
// This is mostly for easier searching and find/replace, but may be counter-productive in some instances
// I tab after { unless it's just one line that is broken for length reasons - reducing tab size may be useful due to deep nesting
// Also, breakout conditionals compress multiple statements onto one line within { } for clarity purposes

// Used to ignore lines under a certain length, as there's some junk past EOF
// It will cause problems with any lines that are valid but short, eg. "Cap"
public class Data_load {
	
	public static final int MIN_LINE_LENGTH = 4;
	
	static //        -----------------------
	//        helper functions
	//        -----------------------
	//
	// These are a few data validation functions, and probably the only code in this file that is easily reusable
	//
	
	/* [Rafael, the Esper]: Not necessary, because Java has equalsIgnoreCase()
	// Pass two Strings to compare
	// Returns a 1 on a match, and a 0 on a fail
	// Throws error on close by not exact matches
	// All comparisons here use ToUpper() so are not case sensitive
	int equ(String hcs_find, String hcs_check)
	{
		if (!strcmp(ToUpper(left(hcs_find, 3)), ToUpper(left(hcs_check, 3)))) // If the first three characters match
		{
			if (!strcmp(ToUpper(hcs_find), ToUpper(hcs_check))) return 1; // Returns success if the Strings match
			if (!strcmp(ToUpper(right(hcs_find, 2)), ToUpper(right(hcs_check, 2)))) ErrorMatchStrings(hcs_find, hcs_check); // Otherwise throws error if the ends match as well
		}
		return 0; // Return a fail to match the Strings
	}
	*/
	
	// Pass a String to check
	// Returns the same String, can be used simply for assignment
	// Throws error if under a set length
	String SafeString(String hss_string)
	{
		if(len(hss_string) < MICRO_STRING) ErrorLoadString(hss_string); // If the String is under a set length, throw an error
		return hss_string; // Return the same String whatever
	}
	
	// Pass a String to check
	// Returns a String in signed format :"+4" or "-25"
	// Throws errors if the format needs to be changed or the value 0
	static String SafeStringVal(String hssv_string)
	{
		String hssv_out = "";
		int hssv_num = val(hssv_string); // Gets number from the String
		if (hssv_num > 0) hssv_out = "+" + str(hssv_num); // Output is a plus sign then the number if positive
		else hssv_out = str(hssv_num); // Otherwise output is the number as minus is always added
		if (!strcmp(hssv_string, hssv_out)) ErrorLoadNumber(hssv_num, hssv_string); // If the output doesn't match the original throws an error
		if (hssv_num == 0) ErrorStupidNumber(hssv_num, hssv_string); // If the number is zero throw an error
		return hssv_out; // Return the output String
	}
	
	// Pass a String expected to be a number
	// Returns the number
	// Throws error if there is any discrepancy between the content
	static int SafeVal(String hsv_string)
	{
		int hsv_num = val(hsv_string); // Gets number from the String
		if (!strcmp(hsv_string.trim(), str(hsv_num).trim())) ErrorLoadNumber(hsv_num, hsv_string); // If a String of the number doesn't match the original throws an error
		return hsv_num;
	}
	
	// Pass a stat define
	// Returns a String representation
	// Throws error if define is unrecognised
	public static String GetStatName(int statdef)
	{
		if (statdef == STAT_MAX_HP) return "MHP";
		if (statdef == STAT_MAX_MP) return "MMP";
		if (statdef == STAT_STR) return "STR";
		if (statdef == STAT_END) return "END";
		if (statdef == STAT_MAG) return "MAG";
		if (statdef == STAT_MGR) return "MGR";
		if (statdef == STAT_HIT) return "HIT";
		if (statdef == STAT_DOD) return "DOD";
		if (statdef == STAT_MBL) return "MBL";
		if (statdef == STAT_FER) return "FER";
		if (statdef == STAT_REA) return "REA";
		if (statdef == STAT_ATK) return "ATK";
		if (statdef == STAT_DEF) return "DEF";
		error("Runtime error: Getting stat name for def: "+str(statdef));
		return "ERR";
	}
	
	// Pass a stat define
	// Returns a String representation
	// Throws error if define is unrecognised
	public static String GetSlotName(int slotdef)
	{
		if (slotdef == SLOT_LHAND) return "LEFT";
		if (slotdef == SLOT_RHAND) return "RIGHT";
		if (slotdef == SLOT_BODY) return "BODY";
		if (slotdef == SLOT_ACC1) return "ACC1";
		if (slotdef == SLOT_ACC2) return "ACC2";
		error("Runtime error: Getting slot name for def: "+str(slotdef));
		return "ERR";
	}
	
	//        -------------------------------
	//        DumpStruct functions
	//        -------------------------------
	//
	// No comments, as a dump is pretty much just a dump
	
	static void DumpStructCast()
	{
		int dsca_i, dsca_j;
		String dsca_s;
		for (dsca_i = 0; dsca_i < MAX_CAST; dsca_i++)
		{
			dsca_s = master_cast[dsca_i].name;
			dsca_s = dsca_s + " | " + master_classes[master_cast[dsca_i].class_ref].name;
			dsca_s = dsca_s + " | " + master_cast[dsca_i].chrname;
			dsca_s = dsca_s + " | "+chr(34) + master_cast[dsca_i].desc + chr(34);
			log(dsca_s);
			dsca_s = "First line only";
			for (dsca_j = 0; dsca_j < MAX_GROWABLE_STATS; dsca_j++)
			{
			dsca_s = dsca_s + " | "+GetStatName(dsca_j)+": " + str(master_cast[dsca_i].stat_growth[0][dsca_j]);
			}
			log(dsca_s);
		}
	}
	
	static void DumpStructClass()
	{
		int dscl_i, dscl_j;
		String dscl_s;
		for (dscl_i = 0; dscl_i < MAX_CLASSES; dscl_i++)
		{
			dscl_s = master_classes[dscl_i].name;
			dscl_s = dscl_s + " | "+chr(34) + master_classes[dscl_i].desc + chr(34);
			dscl_s = dscl_s + " | " + str(master_classes[dscl_i].icon);
			for (dscl_j = 0; dscl_j < MAX_SKILLGROUPS_PER_CLASS; dscl_j++)
			{
				if (master_classes[dscl_i].skill_groups[dscl_j] >= 0)
				{ dscl_s = dscl_s + " | " + master_skilltypes[master_classes[dscl_i].skill_groups[dscl_j]].name; }
				else dscl_s = dscl_s + " | -";
			}
			log(dscl_s);
		}
	}
	
	static void DumpStructItems()
	{
		int dsit_i, dsit_j;
		String dsit_s;
		for (dsit_i = 0; dsit_i < MAX_ITEMS; dsit_i++)
		{
			dsit_s = master_items[dsit_i].name;
			dsit_s = dsit_s + " | " + str(master_items[dsit_i].icon);
			dsit_s = dsit_s + " | " + str(master_items[dsit_i].use_flag);
			dsit_s = dsit_s + " | " + master_items[dsit_i].target_func;
			dsit_s = dsit_s + " | " + master_items[dsit_i].effect_func;
			dsit_s = dsit_s + " | " + str(master_items[dsit_i].price);
			dsit_s = dsit_s + " | "+chr(34) + master_items[dsit_i].desc + chr(34);
			log(dsit_s);
			dsit_s = master_items[dsit_i].equ_modcode;
			dsit_s = dsit_s + " | " + str(master_items[dsit_i].equ_slot);
			for (dsit_j = 0; dsit_j < MAX_CLASSES; dsit_j++)
			{
				if (master_items[dsit_i].equ_classes[dsit_j] > 0)
				{ dsit_s = dsit_s + " | " + master_classes[dsit_j].name; }
				else dsit_s = dsit_s + " | -";
			}
			log(dsit_s);
		}
	}
	
	static void DumpStructSkills()
	{
		int dssk_i;
		String dssk_s;
		for (dssk_i = 0; dssk_i < MAX_SKILLS; dssk_i++)
		{
			dssk_s = master_skills[dssk_i].name;
			dssk_s = dssk_s + " | " + master_skilltypes[master_skills[dssk_i].type].name;
			dssk_s = dssk_s + " | " + str(master_skills[dssk_i].icon);
			dssk_s = dssk_s + " | " + str(master_skills[dssk_i].use_flag);
			dssk_s = dssk_s + " | " + master_skills[dssk_i].target_func;
			dssk_s = dssk_s + " | " + master_skills[dssk_i].effect_func;
			dssk_s = dssk_s + " | " + str(master_skills[dssk_i].mp_cost);
			dssk_s = dssk_s + " | "+chr(34) + master_skills[dssk_i].desc + chr(34);
			log(dssk_s);
		}
	}
	
	static void DumpStructSkillTypes()
	{
		int dsst_i;
		String dsst_s;
		for (dsst_i = 0; dsst_i < MAX_SKILLTYPES; dsst_i++)
		{
			dsst_s = master_skilltypes[dsst_i].name;
			dsst_s = dsst_s + " | "+ chr(34) + master_skilltypes[dsst_i].desc + chr(34);
			dsst_s = dsst_s + " | " + str(master_skilltypes[dsst_i].icon);
			log(dsst_s);
		}
	}
	
	//        ------------------------------
	//        LoadStruct functions
	//        ------------------------------
	//
	// These functions share a lot of general code, but are structure specific
	// If either input data format or structure changes, this code also needs changing
	// Remove the FlagSet() and ShowFlagScreen() lines for usage outside the test bed
	// Load order: SkillTypes, Class, Cast, Skills, Items, Equip
	
	// Loads data from Cast.dat into the master_classes structure
	// Returns the number of entries loaded, or 0 on a fatal error
	// A valid data entry (minus comment):
	// Sara		Engineer	Sara.dat	"She has a tool!"
	// A valid data entry for the stats growth file (minus comment):
	// 3	11	11	3	2	3	1	1	3	3	0	0	2
	static int LoadStructCast()
	{
		global_linenum = 1; // Starting at line 1 of file
		String lsca_token = "\t"; //"	 ,|;:"; // Valid entry separation tokens
		int lsca_count = 0; // No entries yet loaded
		int lsca_die = COUNT_OUT; // Sets the maximum number of junk lines allowed before abort
		int lsca_substore; // Holds the line count of Cast.dat file while the growth data is being loaded
		int lsca_i, lsca_j; // Various iterators for searching through data
		
		for(int i=0; i<master_cast.length; i++) 
			master_cast[i] = new Cast();		
		
		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(load(dat_directory+"Cast.dat").openStream())); // Attempts to open file in read mode
			String lsca_string = fin.readLine(); // Reads the first line

			String lsca_word = gettoken(lsca_string, lsca_token, 0); // Gets first entry of first line
			while (!strcmp("END_OF_FILE", lsca_word)) { // While the EOF token isn't found
				if (!strcmp("//", left(lsca_word, 2)) && len(lsca_string) >= MIN_LINE_LENGTH) // If the line is an entry not junk
				{
					if (lsca_count + 1 == MAX_CAST) { ErrorLoadOver(MAX_CAST, "Cast.dat"); fin.close(); return 0; } // Throws error on overload and aborts
					for (lsca_i = 0; lsca_i < lsca_count; lsca_i++) // Loop through previous values
					{
						if(strcmp(master_cast[lsca_i].name, lsca_word)) // Checks for duplicate name
						{
							lsca_word = lsca_word + str(lsca_count); // Appends number to duplicate
							ErrorDuplicateName("master_cast["+str(lsca_i)+"].name from Cast.dat", master_cast[lsca_i].name+" to "+lsca_word); // Throws error
						}
					}
					master_cast[lsca_count].name = lsca_word;
		
					lsca_word = gettoken(lsca_string, lsca_token, 1);
					for (lsca_i = 0; lsca_i < MAX_CLASSES; lsca_i++) // Loops through classes
					{
						if (master_classes[lsca_i].name.equalsIgnoreCase(lsca_word)) // Checks if the class matches
						{
							master_cast[lsca_count].class_ref = lsca_i; // Sets the pointer to the class
							lsca_i = MAX_CLASSES + 1; // Breaks out of loop
						}
					}
					if (lsca_i == MAX_CLASSES) // If no class matched
					{
						ErrorLoadType("master_cast["+str(lsca_count)+"].class_ref from Cast.dat", lsca_word); // Throws error
						master_cast[lsca_count].class_ref = 0-1;
					}
		
					master_cast[lsca_count].chrname = chr_dir + SafeString(gettoken(lsca_string, lsca_token, 2));
					
					master_cast[lsca_count].desc = SafeString(gettoken(lsca_string, chr(34), 1));
		
					for(lsca_i = 0; lsca_i < MAX_EQUIP_SLOTS; lsca_i++ ) // Set all the equipment slots to empty (-1)
					{
						master_cast[lsca_count].equipment[lsca_i] = 0-1;
					}
		
					lsca_word = gettoken(lsca_string, lsca_token, 3);

					BufferedReader fing = new BufferedReader(new InputStreamReader(load(cast_dat_directory+lsca_word).openStream())); 
					
					if (fing==null || !fing.ready()) ErrorLoadFile(lsca_word); // Throws error on fail
					else // If succeeds loads growth data
					{
						lsca_i = 0; // No entries yet loaded
						lsca_substore = global_linenum; // Stores the current line number of cast.dat file
						global_linenum = 1; // Starting at line 1 of file// Sets line number to first
						lsca_die = COUNT_OUT; // Resets number of junk lines allowed
						lsca_string = fing.readLine(); // Reads first line
						while (!strcmp("END_OF_FILE", gettoken(lsca_string, lsca_token, 0))) // While the EOF token isn't found
						{
							if (!strcmp("//", left(lsca_string, 2)) && len(lsca_string) >= MIN_LINE_LENGTH) // If the line is an entry not junk
							{
								if (lsca_count + 1 == MAX_LEVELS) { ErrorLoadOver(MAX_LEVELS, lsca_word); fing.close(); fin.close(); return 0; } // Throws error on overload and aborts
								master_cast[lsca_count].exp_require[lsca_i] = SafeVal(gettoken(lsca_string, lsca_token, 1));
								for (lsca_j = 0; lsca_j < MAX_GROWABLE_STATS; lsca_j++) // Loops through stats
								{
									master_cast[lsca_count].stat_growth[lsca_i][lsca_j] = SafeVal(gettoken(lsca_string, lsca_token, lsca_j + 2));
								}
								lsca_i++; // Increments current data entry
							}
							else // If the line is not a valid entry
							{
								lsca_die--; // Decrease allowed junk counter
								if (lsca_die < 1) { ErrorCountOut(lsca_word); fing.close(); fin.close(); return 0; } // Throws a past end of file error and aborts
							}
							global_linenum++; // Increments line number being read in file
							lsca_string = fing.readLine(); // Reads next line
						}
						while (lsca_i < MAX_LEVELS) // Clears remaining data in struct
						{
							for (lsca_j = 0; lsca_j < MAX_GROWABLE_STATS; lsca_j++) master_cast[lsca_count].stat_growth[lsca_i][lsca_j] = 0-1;
							master_cast[lsca_count].exp_require[lsca_i] = 0-1;
							lsca_i++;
						}
						lsca_die = COUNT_OUT; // Reset count out protection
						global_linenum = lsca_substore; // Recover current file line number
						fing.close(); // Closes the growth file
					}
		
					FlagSet(lsca_count + 64); // Remove if not in test bed
					ShowFlagScreen(); // Remove if not in test bed
					lsca_count++; // Increments current data entry
				}
				else // If the line is not a valid entry
				{
					lsca_die--; // Decrease allowed junk counter
					if (lsca_die < 1) { ErrorCountOut("Cast.dat"); fin.close(); return 0; } // Throws a past end of file error and aborts
				}
				global_linenum++; // Increments line number being read in file
				lsca_string = fin.readLine(); // Reads next line
				lsca_word = gettoken(lsca_string, lsca_token, 0); // Gets the first entry
			}			

			fin.close(); // Closes file
			lsca_die = lsca_count; // Preserves count valid data loaded
			while (lsca_die < MAX_CAST) // Clears remaining data in struct
			{
				master_cast[lsca_die].name = "";
				master_cast[lsca_die].class_ref = 0-1;
				master_cast[lsca_die].desc = "";
				lsca_die++;
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
			ErrorLoadFile("Cast.dat"); return 0; // Throws error on fail and aborts
		}
		return lsca_count;
	}
	
	// Loads data from Class.dat into the master_classes structure
	// Returns the number of entries loaded, or 0 on a fatal error
	// A valid data entry (minus comment):
	// Funkmaster	"Master of all things groovy, baby."
	// Jive, Fuschia_Magic
	static int LoadStructClass()
	{
		global_linenum = 1; // Starting at line 1 of file
		String lscl_token = "\t"; //"	 ,|;:"; // Valid entry separation tokens
		int lscl_count = 0; // No entries yet loaded
		int lscl_die = COUNT_OUT; // Sets the maximum number of junk lines allowed before abort
		int lscl_skill, lscl_type; // Various iterators for searching through data

		for(int i=0; i<master_classes.length; i++) 
			master_classes[i] = new Data().new CharClass();

		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(load(dat_directory+"Class.dat").openStream())); 
			String lscl_string = fin.readLine(); // Reads the first line
		
			String lscl_word = gettoken(lscl_string, lscl_token, 0); // Gets first entry of first line

			while (!strcmp("END_OF_FILE", lscl_word)) // While the EOF token isn't found
			{
				if (!strcmp("//", left(lscl_word, 2)) && len(lscl_string) >= MIN_LINE_LENGTH) // If the line is an entry not junk
				{
					if (lscl_count + 1 == MAX_CLASSES) { ErrorLoadOver(MAX_CLASSES, "Class.dat"); fin.close(); return 0; } // Throws error on overload and aborts
					for (lscl_type = 0; lscl_type < lscl_count; lscl_type++) // Loop through previous values
					{
						if(strcmp(master_classes[lscl_type].name, lscl_word)) // Checks for duplicate names
						{
							lscl_word = lscl_word + str(lscl_count); // Appends number to duplicate
							ErrorDuplicateName("master_classes["+str(lscl_type)+"].name from Class.dat", master_classes[lscl_type].name+" to "+lscl_word); // Throws errors
						}
					}
					master_classes[lscl_count].name = lscl_word;
		
					master_classes[lscl_count].desc = SafeString(gettoken(lscl_string, chr(34), 1));
					
					lscl_string = fin.readLine(); // Read the next line which should contain the skill groups a class has
					for (lscl_skill = 0; lscl_skill < MAX_SKILLGROUPS_PER_CLASS; lscl_skill++) // Loop through allowable number of skill types per class
					{
						lscl_word = gettoken(lscl_string, lscl_token , lscl_skill);
						for (lscl_type = 0; lscl_type < MAX_SKILLTYPES; lscl_type++) // Loop through skill types
						{
							if (master_skilltypes[lscl_type].name.equalsIgnoreCase(lscl_word)) // Compare value with names
							{
								master_classes[lscl_count].skill_groups[lscl_skill] = lscl_type; // Set pointer to the skill types
								lscl_type = MAX_SKILLTYPES + 1; // Break out of loop
							}
						}
						if (lscl_type == MAX_SKILLTYPES) // If no match was found
						{
							if (lscl_skill == 0) // If this is the first entry
							{
								if (!"NO_SKILLS".equalsIgnoreCase(lscl_word)) // And its not the null skill define
								{ ErrorLoadType("master_classes["+str(lscl_count)+"].skill_groups[0] from Class.dat", lscl_word); } // Throw error
							}
							master_classes[lscl_count].skill_groups[lscl_skill] = 0-1; // Set skill type to null
						}
					}
		
					FlagsVH.FlagSet(lscl_count + 32); // Remove if not in test bed
					ShowFlagScreen(); // Remove if not in test bed
					lscl_count++; // Increments current data entry
				}
				else // If the line is not a valid entry
				{
					lscl_die--; // Decrease allowed junk counter
					if (lscl_die < 1) { ErrorCountOut("Class.dat"); fin.close(); return 0; } // Throws a past end of file error and aborts
				}
				global_linenum++; // Increments line number being read in file
				lscl_string = fin.readLine(); // Reads next line
				lscl_word = gettoken(lscl_string, lscl_token, 0); // Gets the first entry
			}
			fin.close(); // Closes file
			lscl_die = lscl_count; // Preserves count valid data loaded
			while (lscl_die < MAX_CLASSES) // Clears remaining data in struct
			{
				master_classes[lscl_die].name = "";
				master_classes[lscl_die].desc = "";
				for (lscl_type = 0; lscl_type < MAX_SKILLTYPES; lscl_type++) {
					master_classes[lscl_die].skill_groups = new int[MAX_SKILLTYPES];
					master_classes[lscl_die].skill_groups[lscl_type] = 0-1; // Loops through skill types and nulls
				}
				lscl_die++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			ErrorLoadFile("Class.dat"); return 0; // Throws error on fail and aborts
		}
		
		return lscl_count;
	}
	
	// Loads data from Equip.dat into the master_items structure
	// Returns the number of entries loaded, or 0 on a fatal error
	// A valid data entry (minus comment):
	// Silver_Brace
	// Engineer, Priest
	// accessory
	// DEF +5
	// MGR +2
	static int LoadStructEquip()
	{
		global_linenum = 1; // Starting at line 1 of file
		String lseq_token = "\t"; //"	 ,|;:"; // Valid entry separation tokens
		int lseq_i, lseq_j = 0, lseq_item = 0; // Various iterators for searching through data
		int lseq_count = 0; // No entries yet loaded
		int lseq_pos = 0;
		int lseq_die = COUNT_OUT; // Sets the maximum number of junk lines allowed before abort
		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(load(dat_directory+"Equip.dat").openStream())); 
			String lseq_string = fin.readLine(); // Reads the first line

			String lseq_word = gettoken(lseq_string, lseq_token, 0); // Gets first entry of first line
			while (!strcmp("END_OF_FILE", lseq_word)) // While the EOF token isn't found
			{
				if (!strcmp("//", left(lseq_word, 2)) && len(lseq_string) >= MIN_LINE_LENGTH) // If the line is an entry not junk
				{
					if ((lseq_pos & 3) == 3) // Expecting to read a .equ_modcode line, with allowance for byte flags
					{
						lseq_word = gettoken(lseq_string, lseq_token, 0); // Get word to check against list of attributes
						if (strcmp("MAX_HP", lseq_word)) lseq_j = STAT_MAX_HP; // 0
						else if (strcmp("MAX_MP", lseq_word)) lseq_j = STAT_MAX_MP; // 1
						else if (strcmp("STR", lseq_word)) lseq_j = STAT_STR; // 2
						else if (strcmp("END", lseq_word)) lseq_j = STAT_END; // 3
						else if (strcmp("MAG", lseq_word)) lseq_j = STAT_MAG; // 4
						else if (strcmp("MGR", lseq_word)) lseq_j = STAT_MGR; // 5
						else if (strcmp("HIT", lseq_word)) lseq_j = STAT_HIT; // 6
						else if (strcmp("DOD", lseq_word)) lseq_j = STAT_DOD; // 7
						else if (strcmp("MBL", lseq_word)) lseq_j = STAT_MBL; // 8
						else if (strcmp("FER", lseq_word)) lseq_j = STAT_FER; // 9
						else if (strcmp("REA", lseq_word)) lseq_j = STAT_REA; // 10
						else if (strcmp("ATK", lseq_word)) lseq_j = STAT_ATK; // 11
						else if (strcmp("DEF", lseq_word)) lseq_j = STAT_DEF; // 12
						else lseq_pos = 0; // If it doesn't match these, assume it might be a name and go to check that
						if (lseq_pos !=0) // If it did match an attribute
						{
							lseq_i = pow(2, lseq_j + 2); // Works out a byte flag depending on type of attribute - will die for moar than 30 things
							if ((lseq_pos & lseq_i)!=0) ErrorDuplicateType("master_items["+str(lseq_item)+"].equ_modcode from Equip.dat", lseq_word); // Throw duplicate data error
							lseq_pos = lseq_pos | lseq_i; // Sets byte flag for duplication checking
							master_items[lseq_item].equ_modcode = master_items[lseq_item].equ_modcode + lseq_word + ","; // Adds the attribute type to the modcode
							lseq_word = gettoken(lseq_string, lseq_token, 1); // Gets the following number modifier
							master_items[lseq_item].equ_modcode = master_items[lseq_item].equ_modcode + SafeStringVal(lseq_word) + ";"; // Adds the number modifier to the modcode
						}
					}
					else if (lseq_pos == 2) // Expecting to read a .equ_slot line
					{
						lseq_pos++; // Looks for .equ_modcode line next
						lseq_word = gettoken(lseq_string, lseq_token, 0); // Gets value to compare against slot defines
						if ("accessory".equalsIgnoreCase(lseq_word)) master_items[lseq_item].equ_slot = SLOT_ACC1;
						else if ("left_hand".equalsIgnoreCase(lseq_word)) master_items[lseq_item].equ_slot = SLOT_LHAND;
						else if ("right_hand".equalsIgnoreCase(lseq_word)) master_items[lseq_item].equ_slot = SLOT_RHAND;
						else if ("body".equalsIgnoreCase(lseq_word)) master_items[lseq_item].equ_slot = SLOT_BODY;
						else ErrorLoadDefine("master_items["+str(lseq_item)+"].equ_slot from Equip.dat", lseq_word); // Throws an error if the value doesn't match one of these
					}
					else if (lseq_pos == 1) // Expecting to read a .equ_classes line
					{
						lseq_pos++; // Looks for .equ_slot line next
						lseq_word = gettoken(lseq_string, lseq_token, 0);
		
						if ("ALL_CLASSES".equalsIgnoreCase(lseq_word)) // If it matches the everyone can use define
						{
							for (lseq_i = 0; lseq_i < MAX_CLASSES; lseq_i++) // Loops through all classes
							{ master_items[lseq_item].equ_classes[lseq_i] = 1; } // Sets the item to equipable by class
						}
						else // Otherwise we expect specific class names
						{
							lseq_j = 0; // Start at the first entry on the line
							while (len(lseq_word)>0) // If we haven't reach the end of the line
							{						
								for (lseq_i = 0; lseq_i < MAX_CLASSES; lseq_i++) // Loop through classes
								{
									if (master_classes[lseq_i].name.equalsIgnoreCase(lseq_word)) // Check entry against class names
									{
										if (master_items[lseq_item].equ_classes[lseq_i] == 1) // If the item is already equipable by this class
										{ ErrorDuplicateType("master_items["+str(lseq_item)+"].equ_classes["+str(lseq_i)+"] from Equip.dat", lseq_word); } // Throw duplicate data error
										master_items[lseq_item].equ_classes[lseq_i] = 1; // Sets the item to equipable by class
		
										lseq_i = MAX_CLASSES + 1; // Breaks out of loop
									}
								}
								
								if (lseq_i == MAX_CLASSES) // If no match was found
								{ ErrorLoadType("master_items["+str(lseq_item)+"].equ_classes[?] for "+str(lseq_j)+" from Equip.dat", lseq_word); } // Throw error
		
								lseq_j++; // Move onto next entry on the line
								lseq_word = gettoken(lseq_string, lseq_token, lseq_j); // Get the entry (ZIP I HAD TO HUNT DOWN THIS ERROR AND I HATE YOU 4EVER, LOVE - GRUE)
		
								if ( "//".equalsIgnoreCase(left(lseq_word, 2)) ) lseq_word = ""; // If the entry is a comment, break out of while loop
							}
						}
					}
					if (lseq_pos == 0) // Expecting to read a .name line
					{
						if (lseq_count + 1 == MAX_ITEMS) { ErrorLoadOver(MAX_ITEMS, "Equip.dat"); fin.close(); return 0; } // Throws error on overload and aborts
						lseq_word = gettoken(lseq_string, lseq_token, 0);

						for (lseq_i = 0; lseq_i < MAX_ITEMS; lseq_i++) // Loops through items
						{
							if (master_items[lseq_i].name.equalsIgnoreCase(lseq_word)) // Checks value against item names
							{
								lseq_item = lseq_i; // Sets pointer to the item
								lseq_i = MAX_ITEMS + 1; // Breaks out of loop
							}
						}
						if (lseq_i == MAX_ITEMS) // If no match was found
						{
							ErrorLoadType("master_items[?].name from Equip.dat", lseq_word); // Throws error
						}
						else
						{
							lseq_pos++; // Looks for .equ_classes line next
							FlagsVH.FlagSet(lseq_count + 192); // Remove if not in test bed
							ShowFlagScreen(); // Remove if not in test bed
							lseq_count++; // Increments current data entry
						}
					}
				}
				else // If the line is not a valid entry
				{
					lseq_die--; // Decrease allowed junk counter
					if (lseq_die < 1) { ErrorCountOut("Equip.dat");fin.close(); return 0; } // Throws a past end of file error and aborts
				}
				global_linenum++; // Increments line number being read in file
				lseq_string = fin.readLine(); // Reads next line
				lseq_word = gettoken(lseq_string, lseq_token, 0); // Gets the first entry
			}
			fin.close(); // Closes file			
			
		
		}
		catch(Exception e) {
			e.printStackTrace();
			ErrorLoadFile("Equip.dat"); return 0; // Throws error on fail and aborts
		}


		return lseq_count;
	}
	
	// Loads data from Items.dat into the master_items structure
	// Returns the number of entries loaded, or 0 on a fatal error
	// A valid data entry (minus comment):
	// Pearl_of_Truth 	22	Menu	-		pearl_use	0	"Opens castle gate."
	static int LoadStructItems()
	{
		global_linenum = 1; // Starting at line 1 of file
		String lsit_token = "\t"; //"	 ,|;:"; // Valid entry separation tokens
		int lsit_i; // Misc iterator
		int lsit_count = 0; // No entries yet loaded
		int lsit_die = COUNT_OUT; // Sets the maximum number of junk lines allowed before abort

		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(load(dat_directory+"Items.dat").openStream())); 
			String lsit_string = fin.readLine(); // Reads the first line

			String lsit_word = gettoken(lsit_string, lsit_token, 0); // Gets first entry of first line

			while (!strcmp("END_OF_FILE", lsit_word)) // While the EOF token isn't found
			{
				if (!strcmp("//", left(lsit_word, 2)) && len(lsit_string) >= MIN_LINE_LENGTH) // If the line is an entry not junk
				{
					if (lsit_count + 1 == MAX_ITEMS) { ErrorLoadOver(MAX_ITEMS, "Items.dat"); fin.close(); return 0; } // Throws error on overload and aborts
					for (lsit_i = 0; lsit_i < lsit_count; lsit_i++) // Loop through previous values
					{
						if(strcmp(master_items[lsit_i].name, lsit_word)) // Check for duplicate names
						{
							lsit_word = lsit_word + str(lsit_count); // Appends number to duplicate
							ErrorDuplicateName("master_items["+str(lsit_i)+"].name from Items.dat", master_items[lsit_i].name+" to "+lsit_word); // Throws error
						}
					}
					master_items[lsit_count] = new Item(); // [Rafael, the Esper]
					master_items[lsit_count].name = lsit_word;
					
					master_items[lsit_count].icon = SafeVal(gettoken(lsit_string, lsit_token, 1));
					
					master_items[lsit_count].use_flag = 0; // Defaults the use_flag to null
					lsit_word = gettoken(lsit_string, lsit_token, 2); // Gets value to compare against valid flag defines
					if ("BOTH".equalsIgnoreCase(lsit_word)) master_items[lsit_count].use_flag = USE_BATTLE | USE_MENU;
					else if ("MENU".equalsIgnoreCase(lsit_word)) master_items[lsit_count].use_flag = USE_MENU;
					else if ("BATTLE".equalsIgnoreCase(lsit_word)) master_items[lsit_count].use_flag = USE_BATTLE;
					else if (!strcmp("-", lsit_word)) ErrorLoadDefine("master_items["+str(lsit_count)+"].use_flag from Items.dat", lsit_word); // Throws an error if the value isn't a valid null either
		
					lsit_word = gettoken(lsit_string, lsit_token, 3);
					if (master_items[lsit_count].use_flag !=0 && len(lsit_word) > 3) master_items[lsit_count].target_func = lsit_word;
					else master_items[lsit_count].target_func = ""; // If there's a battle function, store the function name, else wipe the value
					if (master_items[lsit_count].use_flag!=0) master_items[lsit_count].effect_func = SafeString(gettoken(lsit_string, lsit_token, 4));
					else master_items[lsit_count].effect_func = ""; // If there's a use, store the function name, else wipe the value
					
					master_items[lsit_count].price = SafeVal(gettoken(lsit_string, lsit_token, 5));
					
					master_items[lsit_count].desc = SafeString(gettoken(lsit_string, chr(34), 1));
					
					master_items[lsit_count].equ_modcode = "";
					for (lsit_i = 0; lsit_i < MAX_CLASSES; lsit_i++) // Loops through classes that can equip and nulls
					{ master_items[lsit_count].equ_classes[lsit_i] = 0; }
		
					FlagsVH.FlagSet(lsit_count + 128); // Remove if not in test bed
					ShowFlagScreen(); // Remove if not in test bed
					lsit_count++; // Increments current data entry
				}
				else // If the line is not a valid entry
				{
					lsit_die--; // Decrease allowed junk counter
					if (lsit_die < 1) { ErrorCountOut("Items.dat"); fin.close(); return 0; } // Throws a past end of file error and aborts
				}
				global_linenum++; // Increments line number being read in file
				lsit_string =  fin.readLine(); // Reads next line
				lsit_word = gettoken(lsit_string, lsit_token, 0); // Gets the first entry
			}
			
			fin.close(); // Closes file	
			
		} catch(Exception e) {
				e.printStackTrace();
				ErrorLoadFile("Items.dat"); return 0; // Throws error on fail and aborts
		}
		

		
		lsit_die = lsit_count; // Preserves count valid data loaded
		while (lsit_die < MAX_ITEMS) // Clears remaining data in struct
		{
			master_items[lsit_die] = new Item(); // [Rafael, the Esper]
			master_items[lsit_die].name = "";
			master_items[lsit_die].icon = 0-1;
			master_items[lsit_die].use_flag = 0;
			master_items[lsit_die].target_func = "";
			master_items[lsit_die].effect_func = "";
			master_items[lsit_die].price = 0-1;
			master_items[lsit_die].desc = "";
			master_items[lsit_die].equ_modcode = "";
			for (lsit_i = 0; lsit_i < MAX_CLASSES; lsit_i++) // Loops through classes that can equip and nulls
			{ master_items[lsit_die].equ_classes[lsit_i] = 0; }
			lsit_die++;
		}
		return lsit_count;
	}
	
	// Loads data from Skills.dat into the master_skills structure
	// Returns the number of entries loaded, or 0 on a fatal error
	// A valid data entry (minus comment):
	// Ice_1 		Black_Magic 	2	BATTLE	targ_single_nme	ice1_effect	3	"Ice-based Attack."
	static int LoadStructSkills()
	{
		global_linenum = 1; // Starting at line 1 of file
		String lssk_token = "\t"; //"	 ,|;:"; // Valid entry separation tokens
		int lssk_i; // Misc iterator
		int lssk_count = 0; // No entries yet loaded
		int lssk_die = COUNT_OUT; // Sets the maximum number of junk lines allowed before abort

		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(load(dat_directory+"Skills.dat").openStream())); 
			String lssk_string = fin.readLine(); // Reads the first line

			String lssk_word = gettoken(lssk_string, lssk_token, 0); // Gets first entry of first line

			while (!strcmp("END_OF_FILE", lssk_word)) // While the EOF token isn't found
			{
				if (!strcmp("//", left(lssk_word, 2)) && len(lssk_string) >= MIN_LINE_LENGTH) // If the line is an entry not junk
				{
					if (lssk_count + 1 == MAX_SKILLS) { ErrorLoadOver(MAX_SKILLS, "Skills.dat"); fin.close(); return 0; } // Throws error on overload and aborts
					for (lssk_i = 0; lssk_i < lssk_count; lssk_i++) // Loop through previous values
					{
						if(strcmp(master_skills[lssk_i].name, lssk_word)) // Check for duplicate names
						{
							lssk_word = lssk_word + str(lssk_count); // Append number to duplicate
							ErrorDuplicateName("master_skills["+str(lssk_i)+"].name from Skills.dat", master_skills[lssk_i].name+" to "+lssk_word); // Throws error
						}
					}
					master_skills[lssk_count] = new Skill(); // [Rafael, the Esper]
					master_skills[lssk_count].name = lssk_word;
		
					lssk_word = gettoken(lssk_string, lssk_token, 1);
					for (lssk_i = 0; lssk_i < MAX_SKILLTYPES; lssk_i++) // Loops through skill types
					{
						if (master_skilltypes[lssk_i].name.trim().equalsIgnoreCase(lssk_word.trim())) // Checks value against skill type names
						{
							master_skills[lssk_count].type = lssk_i; // Set the pointer to the skill type
							lssk_i = MAX_SKILLTYPES + 1; // Breaks out of loop
						}
					}
					if (lssk_i == MAX_SKILLTYPES) // If no match was found
					{
						master_skills[lssk_count].type = 0-1;
						ErrorLoadType("master_skills["+str(lssk_count)+"].type from Skills.dat", lssk_word); // Throws error
					}
		
					master_skills[lssk_count].icon = SafeVal(gettoken(lssk_string, lssk_token, 2));
		
					master_skills[lssk_count].use_flag = 0; // Defaults the use_flag to null
					lssk_word = gettoken(lssk_string, lssk_token, 3); // Gets value to compare against valid flag defines
					if ("BOTH".equalsIgnoreCase(lssk_word)) master_skills[lssk_count].use_flag = USE_BATTLE | USE_MENU;
					else if ("MENU".equalsIgnoreCase(lssk_word)) master_skills[lssk_count].use_flag = USE_MENU;
					else if ("BATTLE".equalsIgnoreCase(lssk_word)) master_skills[lssk_count].use_flag = USE_BATTLE;
					else ErrorLoadDefine("master_skills["+str(lssk_count)+"].use_flag from Skills.dat", lssk_word); // Throws an error if the value doesn't match one of these
		
					lssk_word = gettoken(lssk_string, lssk_token, 4);
					if (master_skills[lssk_count].use_flag!=0 && len(lssk_word) > 3) master_items[lssk_count].target_func = lssk_word;
					else master_skills[lssk_count].target_func = ""; // If there's a battle function, store the function name, else wipe the value
					if (master_skills[lssk_count].use_flag!=0) master_items[lssk_count].effect_func = SafeString(gettoken(lssk_string, lssk_token, 5));
					else master_skills[lssk_count].effect_func = ""; // If there's a use, store the function name, else wipe the value
		
					master_skills[lssk_count].mp_cost = SafeVal(gettoken(lssk_string, lssk_token, 6));
		
					master_skills[lssk_count].desc = SafeString(gettoken(lssk_string, chr(34), 1));
		
					FlagSet(lssk_count + 96); // Remove if not in test bed
					ShowFlagScreen(); // Remove if not in test bed
					lssk_count++; // Increments current data entry
				}
				else // If the line is not a valid entry
				{
					lssk_die--; // Decrease allowed junk counter
					if (lssk_die < 1) { ErrorCountOut("Skills.dat"); fin.close(); return 0; } // Throws a past end of file error and aborts
				}
				global_linenum++; // Increments line number being read in file
				lssk_string = fin.readLine(); // Reads next line
				lssk_word = gettoken(lssk_string, lssk_token, 0); // Gets the first entry
			}
			
			fin.close(); // Closes file	
			
		} catch(Exception e) {
				e.printStackTrace();
				ErrorLoadFile("Skills.dat"); return 0; // Throws error on fail and aborts
		}
	
		
		lssk_die = lssk_count; // Preserves count valid data loaded
		while (lssk_die < MAX_SKILLS) // Clears remaining data in struct
		{
			master_skills[lssk_die] = new Skill(); // [Rafael, the Esper]
			master_skills[lssk_die].name = "";
			master_skills[lssk_die].icon = 0-1;
			master_skills[lssk_die].type = 0-1;
			master_skills[lssk_die].use_flag = 0;
			master_skills[lssk_die].target_func = "";
			master_skills[lssk_die].effect_func = "";
			master_skills[lssk_die].mp_cost = 0-1;
			master_skills[lssk_die].desc = "";
			lssk_die++;
		}
		return lssk_count;
	}
	
	// Loads data from Skilltypes.dat into the master_skilltypes structure
	// Returns the number of entries loaded, or 0 on a fatal error
	// A valid data entry (minus comment):
	// Fuschia_Magic	3	"The magic of interior decorating!"
	static int LoadStructSkillTypes()
	{
		global_linenum = 1; // Starting at line 1 of file
		String lsst_token = "\t"; //"	 ,|;:"; // Valid entry separation tokens
		int lsst_i; // Misc iterator
		int lsst_count = 0; // No entries yet loaded
		int lsst_die = COUNT_OUT; // Sets the maximum number of junk lines allowed before abort

		for(int i=0; i<master_skilltypes.length; i++) 
			master_skilltypes[i] = new Data().new SkillType();
		
		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(load(dat_directory+"Skilltypes.dat").openStream())); 
			String lsst_string = fin.readLine(); // Reads the first line
			
			String lsst_word = gettoken(lsst_string, lsst_token, 0); // Gets first entry of first line
			while (!strcmp("END_OF_FILE", lsst_word)) // While the EOF token isn't found
			{
				if (!strcmp("//", left(lsst_word, 2)) && len(lsst_string) >= MIN_LINE_LENGTH) // If the line is an entry not junk
				{
					if (lsst_count + 1 == MAX_SKILLTYPES) { ErrorLoadOver(MAX_SKILLTYPES, "Skilltypes.dat"); fin.close(); return 0; } // Throws error on overload and aborts
					for (lsst_i = 0; lsst_i < lsst_count; lsst_i++) // Loop through previous values
					{
						if(strcmp(master_skilltypes[lsst_i].name, lsst_word)) // Check for duplicate names
						{
							lsst_word = lsst_word + str(lsst_count); // Append number to duplicate
							ErrorDuplicateName("master_skilltypes["+str(lsst_i)+"].name from Skilltypes.dat", master_skilltypes[lsst_i].name+" to "+lsst_word); // Throws error
						}
					}
					master_skilltypes[lsst_count].name = lsst_word;
		
					master_skilltypes[lsst_count].icon = SafeVal(gettoken(lsst_string, lsst_token, 1));
		
					master_skilltypes[lsst_count].desc = SafeString(gettoken(lsst_string, chr(34), 1));
		
					FlagSet(lsst_count); // Remove if not in test bed
					ShowFlagScreen(); // Remove if not in test bed
					lsst_count++; // Increments current data entry
				}
				else // If the line is not a valid entry
				{
					lsst_die--; // Decrease allowed junk counter
					if (lsst_die < 1) { ErrorCountOut("Skilltypes.dat"); fin.close(); return 0; } // Throws a past end of file error and aborts
				}
				global_linenum++; // Increments line number being read in file
				lsst_string = fin.readLine(); // Reads next line
				lsst_word = gettoken(lsst_string, lsst_token, 0); // Gets the first entry
			}
			fin.close(); // Closes file
			lsst_die = lsst_count; // Preserves count valid data loaded
			while (lsst_die < MAX_SKILLTYPES) // Clears remaining data in struct
			{
				master_skilltypes[lsst_die].name = "";
				master_skilltypes[lsst_die].icon = 0-1;
				master_skilltypes[lsst_die].desc = "";
				lsst_die++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorLoadFile("Skilltypes.dat"); return 0; // Throws error on fail and aborts
		}
		return lsst_count;
	}
}