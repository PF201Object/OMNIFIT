#!/bin/bash

# --- Configuration for Portability ---
# CRUCIAL STEP: Get the directory of the script, regardless of where it's called from.
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Define relative paths pointing to the Project Folder, Java File, and Database file
NETBEANS_PROJECT_PATH="$SCRIPT_DIR/GymMembershipSystem"
JAVA_FILE_PATH="$SCRIPT_DIR/GymMembershipSystem/src/main/main.java"  # Adjust if the exact path differs (e.g., src/main.java)
DATABASE_FILE_PATH="$SCRIPT_DIR/GymMembershipSystem/GMS.db"

# Store the default browser launch URL (replace with your actual links)
FLOWCHART_URL="https://example.com/flowchart"
MANUAL_URL="https://example.com/manual"

# --- Function to convert path for Windows' 'start' command ---
win_path() {
    if command -v cygpath &> /dev/null; then
        cygpath -w "$1"
    else
        echo "$1"
    fi
}

# --- Function to open Java file directly (using system association - this should work automatically) ---
open_java_file() {
    local java_path="$1"
    if [ ! -f "$java_path" ]; then
        echo "⚠️ Java file not found: $java_path"
        return 1
    fi
    
    local win_java_path=$(win_path "$java_path")
    echo "Attempting to open: $win_java_path"
    echo "Command: start \"\" \"$win_java_path\""
    
    # Directly use 'start' to open the file with default association (NetBeans if .java is associated)
    start "" "$win_java_path" 2>/dev/null &
    LAUNCH_SUCCESS=$?
    
    if [ $LAUNCH_SUCCESS -eq 0 ]; then
        echo "✅ Java file opened successfully (should launch in NetBeans or default editor)."
        return 0
    else
        echo "⚠️ Failed to open Java file (exit code: $LAUNCH_SUCCESS)."
        return 1
    fi
}

# --- Function to open project folder in Windows Explorer (fallback) ---
open_in_explorer() {
    local project_path="$1"
    local win_project_path=$(win_path "$project_path")
    
    echo "Opening project folder in Windows Explorer as fallback: $win_project_path"
    echo "Command: explorer \"$win_project_path\""
    
    if command -v explorer &> /dev/null; then
        explorer "$win_project_path" 2>/dev/null &
        EXPLORER_SUCCESS=$?
        if [ $EXPLORER_SUCCESS -eq 0 ]; then
            echo "✅ Project folder opened in Explorer."
            echo "   - Double-click main.java to open it in NetBeans (since file association works)."
            echo "   - Or, in a running NetBeans: File > Open Project... > Select this folder."
        else
            echo "⚠️ Failed to open Explorer (exit code: $EXPLORER_SUCCESS)."
        fi
        return $EXPLORER_SUCCESS
    else
        echo "⚠️ Explorer not available. Manually navigate to: $win_project_path"
        return 1
    fi
}

# --- Function to display the menu ---
display_menu() {
    clear
    echo -e "\e[92m" # Set color for the header
    echo "             -------------------------"
    echo "             | GYM MEMBERSHIP SYSTEM |"
    echo "             -------------------------"
    echo ""
    echo -e "\e[93m" # Set color for the menu options
    echo "1. Java Package (Open main.java - Auto in NetBeans)"
    echo "2. Database"
    echo "3. FlowChart"
    echo "4. Manual"
    echo ""
}

# --- Main loop for the menu ---
while true; do
    
    display_menu
    
    read -p "Choose an option (1-4): " choice
    
    ACTION_TAKEN=false # Reset flag at the start of the loop

    case $choice in
        1)
            echo "Opening Java Package (main.java)..."
            
            # Check if project folder exists (basic structure check)
            if [ ! -d "$NETBEANS_PROJECT_PATH" ]; then
                echo "⚠️ Project folder not found: $NETBEANS_PROJECT_PATH"
                echo "Please ensure 'GymMembershipSystem' folder is next to this script."
                ACTION_TAKEN=true
                sleep 3
                continue
            fi
            
            # Primary: Open the .java file directly via system association (this should work as you described)
            if open_java_file "$JAVA_FILE_PATH"; then
                echo "If NetBeans didn't open, check:"
                echo "- .java files are associated with NetBeans (Right-click main.java > Open with > Choose NetBeans)."
                echo "- Run this in Git Bash manually: start \"\" \"$(win_path "$JAVA_FILE_PATH")\""
            else
                # Fallback: Open project folder in Explorer
                open_in_explorer "$NETBEANS_PROJECT_PATH"
                echo ""
                echo "Troubleshooting:"
                echo "1. Verify file path: Run 'ls -la \"$JAVA_FILE_PATH\"' in Git Bash."
                echo "2. Test manual open: In Git Bash, cd to project and run 'start \"\" main.java'."
                echo "3. If association issue: In Windows, right-click .java > Properties > Change > NetBeans."
            fi

            ACTION_TAKEN=true
            sleep 4  # Pause to read output and commands
            ;;
        2)
            echo "Opening SQLite Database..."
            if [ -f "$DATABASE_FILE_PATH" ]; then
                WIN_DB_PATH=$(win_path "$DATABASE_FILE_PATH")  # Removed 'local' - not in a function
                echo "Opening: $WIN_DB_PATH"
                start "" "$WIN_DB_PATH" 2>/dev/null || \
                echo "⚠️ Could not open database. Install/associate DB Browser for SQLite with .db files."
            else
                echo "⚠️ Database not found: $DATABASE_FILE_PATH"
                echo "Check if GMS.db is in GymMembershipSystem/."
            fi
            ACTION_TAKEN=true
            ;;
        3) 
            echo "Opening FlowChart..." 
            start "" "$FLOWCHART_URL" 2>/dev/null || xdg-open "$FLOWCHART_URL" 2>/dev/null || open "$FLOWCHART_URL" 2>/dev/null || \
            echo "⚠️ Could not open FlowChart. Replace URL with actual link or local file path."
            ACTION_TAKEN=true
            ;;
        4) 
            echo "Opening Manual Website..."
            start "" "$MANUAL_URL" 2>/dev/null || xdg-open "$MANUAL_URL" 2>/dev/null || open "$MANUAL_URL" 2>/dev/null || \
            echo "⚠️ Could not open Manual. Replace URL with actual link or local file path."
            ACTION_TAKEN=true
            ;;
        *)
            echo -e "\e[31mInvalid option. Try again.\e[0m"
            ACTION_TAKEN=false
            sleep 1.5
            ;;
    esac

    # Prompt to continue only if a valid option (1-4) was picked
    if [ "$ACTION_TAKEN" = true ]; then
        echo ""
        read -p "Do you want to pick again? (Yes/No): " REPICK
        
        case ${REPICK,,} in
            yes|y)
                continue
                ;;
            *)
                echo "Exiting system. Goodbye!"
                break
                ;;
        esac
    fi

done

echo -e "\e[0m" # Reset text color