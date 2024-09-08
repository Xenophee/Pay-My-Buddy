/** @type {import("tailwindcss").Config} */

const defaultTheme = require('tailwindcss/defaultTheme')


module.exports = {
	content: [
		"./src/main/resources/templates/**/*.html",
		"./src/main/resources/static/**/*.{css,js}",
	],
	theme: {
		extend: {
			fontFamily: {
				'sans': ['Roboto', ...defaultTheme.fontFamily.sans],
				'special': ['Calistoga', 'sans-serif'],
			}
		}
	},
	plugins: [require('@tailwindcss/forms')],
};

