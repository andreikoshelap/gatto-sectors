import fs from 'fs';
import * as cheerio from 'cheerio';

const html = fs.readFileSync('index.html', 'utf8');
const $ = cheerio.load(html);

const sectors = [];

$('select option').each((i, el) => {
    const value = $(el).attr('value');
    const raw = $(el).html();

    const indent = (raw.match(/&nbsp;/g) || []).length / 4;
    const name = raw.replace(/&nbsp;/g, '').trim();

    sectors.push({ id: Number(value), name, indent });
});

console.log(sectors);
