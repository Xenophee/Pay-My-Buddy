const INCREMENT = 1;
const DECREMENT = -1;
const MIN_VALUE = 0;

const updateAmount = (change) => {
	let amountValue = Number(amount.value) || 0;
	amountValue += change;

	if (amountValue < MIN_VALUE) amountValue = MIN_VALUE;

	amount.value = amountValue;
}

moreAmount.addEventListener('click', () => updateAmount(INCREMENT));
lessAmount.addEventListener('click', () => updateAmount(DECREMENT));
