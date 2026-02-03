/**
 * Extracts a user-friendly error message from an Axios error object.
 * Handles backend error responses in the format: { code: string, message: string, timestamp: number }
 * 
 * @param {Error} error - The error object from Axios
 * @param {string} fallbackMessage - Message to use if no specific message can be extracted
 * @returns {string} The extracted error message
 */
export const getErrorMessage = (error, fallbackMessage = 'An unexpected error occurred') => {
    // If the error has a response from the server
    if (error.response && error.response.data) {
        // Handle { message: "..." } or { error: "..." }
        const data = error.response.data;
        return data.message || data.error || fallbackMessage;
    }

    // If there was no response (network error)
    if (error.request) {
        return 'Network error. Please check your connection.';
    }

    // Something else happened
    return error.message || fallbackMessage;
};
