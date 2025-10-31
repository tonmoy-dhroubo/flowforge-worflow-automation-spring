// get-codes.js

const fs = require('fs');
const path = require('path');

// --- CONFIGURATION ---

// The name of the final output file.
const OUTPUT_FILE = 'project_context.txt';

// List of directories and files to completely ignore.
// This is a blacklist and takes precedence.
const IGNORE_LIST = [
    '.git',
    '.idea',
    'node_modules',
    'dist',
    'build',
    '.DS_Store',
    'target',
    'test',
    OUTPUT_FILE // important to avoid including the output file itself
];

// List of file extensions to INCLUDE. This is a whitelist.
// An empty array [] means ALL file extensions will be included.
// Example: ['.js', '.html', '.css', '.json']
const WHITELIST_EXTENSIONS = [
    '.html', '.css', '.scss', '.less',
    '.json', '.yml', '.yaml',
    '.java', '.properties', '.xml',
    '.sql', '.md',
    '.py', '.sh'
];


// --- MAIN SCRIPT LOGIC ---

// 1. Get the project folder name from command line arguments.
const projectFolder = process.argv[2];
if (!projectFolder) {
    console.error('❌ Error: Please provide the project folder name.');
    console.log('Usage: node get-codes.js <project-folder-name>');
    process.exit(1);
}

const projectPath = path.resolve(projectFolder);

// 2. Check if the provided folder actually exists.
if (!fs.existsSync(projectPath) || !fs.statSync(projectPath).isDirectory()) {
    console.error(`❌ Error: The folder "${projectFolder}" does not exist or is not a directory.`);
    process.exit(1);
}

console.log(`🚀 Starting to process project: ${projectPath}`);
if (WHITELIST_EXTENSIONS.length > 0) {
    console.log(`⚪️ Whitelisting extensions: ${WHITELIST_EXTENSIONS.join(', ')}`);
} else {
    console.log(`⚪️ No extension whitelist. Including all files.`);
}

let finalOutput = '';

/**
 * Checks if a file's extension is in the whitelist.
 * @param {string} file - The name of the file.
 * @returns {boolean}
 */
function isWhitelisted(file) {
    if (WHITELIST_EXTENSIONS.length === 0) {
        return true; // If whitelist is empty, everything is allowed.
    }
    const ext = path.extname(file).toLowerCase();
    return WHITELIST_EXTENSIONS.includes(ext);
}

/**
 * Generates a string representing the folder hierarchy, respecting ignore and whitelist rules.
 * @param {string} dir - The directory to start from.
 * @param {string} prefix - The prefix for indentation (used in recursion).
 * @returns {string} The formatted tree structure.
 */
function generateTree(dir, prefix = '') {
    let tree = '';
    const files = fs.readdirSync(dir);

    const filteredItems = files.filter(file => {
        if (IGNORE_LIST.includes(file)) return false;

        const fullPath = path.join(dir, file);
        const stat = fs.statSync(fullPath);

        if (stat.isDirectory()) {
            return true; // Always include directories not in the ignore list
        }
        // For files, check if they are whitelisted
        return isWhitelisted(file);
    });

    filteredItems.forEach((file, index) => {
        const fullPath = path.join(dir, file);
        const isLast = index === filteredItems.length - 1;
        const connector = isLast ? '└── ' : '├── ';

        tree += prefix + connector + file + '\n';

        if (fs.statSync(fullPath).isDirectory()) {
            const newPrefix = prefix + (isLast ? '    ' : '│   ');
            tree += generateTree(fullPath, newPrefix);
        }
    });
    return tree;
}

/**
 * Recursively finds all whitelisted file paths within a directory.
 * @param {string} dir - The directory to search in.
 * @returns {string[]} An array of full file paths.
 */
function getAllFilePaths(dir) {
    let filePaths = [];
    const items = fs.readdirSync(dir);

    for (const item of items) {
        if (IGNORE_LIST.includes(item)) {
            continue;
        }

        const fullPath = path.join(dir, item);
        const stat = fs.statSync(fullPath);

        if (stat.isDirectory()) {
            filePaths = filePaths.concat(getAllFilePaths(fullPath));
        } else if (isWhitelisted(item)) { // Check if the file extension is whitelisted
            filePaths.push(fullPath);
        }
    }
    return filePaths;
}

try {
    // --- PART A: GENERATE FOLDER HIERARCHY ---
    console.log('🌳 Generating filtered folder hierarchy...');
    const projectName = path.basename(projectPath);
    const tree = generateTree(projectPath, '');

    finalOutput += `This document contains the source code for the project "${projectName}".\n`;
    finalOutput += `Only files with the following extensions are included: ${WHITELIST_EXTENSIONS.join(', ')}\n\n`;
    finalOutput += `The folder structure is as follows:\n\n`;
    finalOutput += `${projectName}\n${tree}\n\n`;
    finalOutput += '----------------------------------------\n\n';

    // --- PART B: GET CODES OF EACH FILE ---
    console.log('📝 Reading contents of whitelisted files...');
    const allFiles = getAllFilePaths(projectPath);

    for (const filePath of allFiles) {
        const relativePath = path.relative(projectPath, filePath);

        finalOutput += `// FILE: ${relativePath}\n`;
        finalOutput += '========================================\n';

        const content = fs.readFileSync(filePath, 'utf8');
        finalOutput += content;

        finalOutput += '\n\n';
    }

    // --- WRITE THE FINAL OUTPUT TO A FILE ---
    fs.writeFileSync(OUTPUT_FILE, finalOutput);
    console.log(`\n✅ Success! All code compiled into: ${path.resolve(OUTPUT_FILE)}`);
    console.log(`You can now copy the contents of that file and paste it into the LLM chat.`);

} catch (error) {
    console.error('\n❌ An unexpected error occurred:');
    console.error(error);
}