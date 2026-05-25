package com.chemistry.demo.config.seeder;

import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.repository.ChemicalCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class ChemicalCardSeeder implements DataSeeder {

    private final ChemicalCardRepository chemicalCardRepository;

    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(3000);

    private static final Set<String> DEFAULT_PURCHASABLE = Set.of(
            "Na", "Mg", "Cl", "Ca", "Fe", "Cu"
    );

    @Override
    @Transactional
    public void seed() {
        for (String[] e : ELEMENTS) {
            Integer atomicNumber = Integer.valueOf(e[0]);

            if (chemicalCardRepository.existsByAtomicNumber(atomicNumber)) {
                continue;
            }

            String symbol = e[1];

            ChemicalCard card = ChemicalCard.builder()
                    .atomicNumber(atomicNumber)
                    .symbol(symbol)
                    .name(e[2])
                    .category(e[3])
                    .atomicMass(new BigDecimal(e[4]))
                    .period(Integer.valueOf(e[5]))
                    .groupNumber(parseNullableInteger(e[6]))
                    .price(DEFAULT_PRICE)
                    .active(true)
                    .purchasable(DEFAULT_PURCHASABLE.contains(symbol))
                    .build();

            chemicalCardRepository.save(card);
        }
    }

    @Override
    public int getOrder() {
        return 6;
    }

    private Integer parseNullableInteger(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("null")) {
            return null;
        }
        return Integer.valueOf(value);
    }

    private static final String[][] ELEMENTS = {
            {"1", "H", "Hydrogen", "Diatomic nonmetal", "1.008", "1", "1"},
            {"2", "He", "Helium", "Noble gas", "4.002602", "1", "18"},
            {"3", "Li", "Lithium", "Alkali metal", "6.94", "2", "1"},
            {"4", "Be", "Beryllium", "Alkaline earth metal", "9.0121831", "2", "2"},
            {"5", "B", "Boron", "Metalloid", "10.81", "2", "13"},
            {"6", "C", "Carbon", "Polyatomic nonmetal", "12.011", "2", "14"},
            {"7", "N", "Nitrogen", "Diatomic nonmetal", "14.007", "2", "15"},
            {"8", "O", "Oxygen", "Diatomic nonmetal", "15.999", "2", "16"},
            {"9", "F", "Fluorine", "Diatomic nonmetal", "18.998403163", "2", "17"},
            {"10", "Ne", "Neon", "Noble gas", "20.1797", "2", "18"},

            {"11", "Na", "Sodium", "Alkali metal", "22.98976928", "3", "1"},
            {"12", "Mg", "Magnesium", "Alkaline earth metal", "24.305", "3", "2"},
            {"13", "Al", "Aluminium", "Post-transition metal", "26.9815385", "3", "13"},
            {"14", "Si", "Silicon", "Metalloid", "28.085", "3", "14"},
            {"15", "P", "Phosphorus", "Polyatomic nonmetal", "30.973761998", "3", "15"},
            {"16", "S", "Sulfur", "Polyatomic nonmetal", "32.06", "3", "16"},
            {"17", "Cl", "Chlorine", "Diatomic nonmetal", "35.45", "3", "17"},
            {"18", "Ar", "Argon", "Noble gas", "39.948", "3", "18"},

            {"19", "K", "Potassium", "Alkali metal", "39.0983", "4", "1"},
            {"20", "Ca", "Calcium", "Alkaline earth metal", "40.078", "4", "2"},
            {"21", "Sc", "Scandium", "Transition metal", "44.955908", "4", "3"},
            {"22", "Ti", "Titanium", "Transition metal", "47.867", "4", "4"},
            {"23", "V", "Vanadium", "Transition metal", "50.9415", "4", "5"},
            {"24", "Cr", "Chromium", "Transition metal", "51.9961", "4", "6"},
            {"25", "Mn", "Manganese", "Transition metal", "54.938044", "4", "7"},
            {"26", "Fe", "Iron", "Transition metal", "55.845", "4", "8"},
            {"27", "Co", "Cobalt", "Transition metal", "58.933194", "4", "9"},
            {"28", "Ni", "Nickel", "Transition metal", "58.6934", "4", "10"},
            {"29", "Cu", "Copper", "Transition metal", "63.546", "4", "11"},
            {"30", "Zn", "Zinc", "Transition metal", "65.38", "4", "12"},
            {"31", "Ga", "Gallium", "Post-transition metal", "69.723", "4", "13"},
            {"32", "Ge", "Germanium", "Metalloid", "72.630", "4", "14"},
            {"33", "As", "Arsenic", "Metalloid", "74.921595", "4", "15"},
            {"34", "Se", "Selenium", "Polyatomic nonmetal", "78.971", "4", "16"},
            {"35", "Br", "Bromine", "Diatomic nonmetal", "79.904", "4", "17"},
            {"36", "Kr", "Krypton", "Noble gas", "83.798", "4", "18"},

            {"37", "Rb", "Rubidium", "Alkali metal", "85.4678", "5", "1"},
            {"38", "Sr", "Strontium", "Alkaline earth metal", "87.62", "5", "2"},
            {"39", "Y", "Yttrium", "Transition metal", "88.90584", "5", "3"},
            {"40", "Zr", "Zirconium", "Transition metal", "91.224", "5", "4"},
            {"41", "Nb", "Niobium", "Transition metal", "92.90637", "5", "5"},
            {"42", "Mo", "Molybdenum", "Transition metal", "95.95", "5", "6"},
            {"43", "Tc", "Technetium", "Transition metal", "98", "5", "7"},
            {"44", "Ru", "Ruthenium", "Transition metal", "101.07", "5", "8"},
            {"45", "Rh", "Rhodium", "Transition metal", "102.90550", "5", "9"},
            {"46", "Pd", "Palladium", "Transition metal", "106.42", "5", "10"},
            {"47", "Ag", "Silver", "Transition metal", "107.8682", "5", "11"},
            {"48", "Cd", "Cadmium", "Transition metal", "112.414", "5", "12"},
            {"49", "In", "Indium", "Post-transition metal", "114.818", "5", "13"},
            {"50", "Sn", "Tin", "Post-transition metal", "118.710", "5", "14"},
            {"51", "Sb", "Antimony", "Metalloid", "121.760", "5", "15"},
            {"52", "Te", "Tellurium", "Metalloid", "127.60", "5", "16"},
            {"53", "I", "Iodine", "Diatomic nonmetal", "126.90447", "5", "17"},
            {"54", "Xe", "Xenon", "Noble gas", "131.293", "5", "18"},

            {"55", "Cs", "Caesium", "Alkali metal", "132.90545196", "6", "1"},
            {"56", "Ba", "Barium", "Alkaline earth metal", "137.327", "6", "2"},
            {"57", "La", "Lanthanum", "Lanthanide", "138.90547", "6", "null"},
            {"58", "Ce", "Cerium", "Lanthanide", "140.116", "6", "null"},
            {"59", "Pr", "Praseodymium", "Lanthanide", "140.90766", "6", "null"},
            {"60", "Nd", "Neodymium", "Lanthanide", "144.242", "6", "null"},
            {"61", "Pm", "Promethium", "Lanthanide", "145", "6", "null"},
            {"62", "Sm", "Samarium", "Lanthanide", "150.36", "6", "null"},
            {"63", "Eu", "Europium", "Lanthanide", "151.964", "6", "null"},
            {"64", "Gd", "Gadolinium", "Lanthanide", "157.25", "6", "null"},
            {"65", "Tb", "Terbium", "Lanthanide", "158.92535", "6", "null"},
            {"66", "Dy", "Dysprosium", "Lanthanide", "162.500", "6", "null"},
            {"67", "Ho", "Holmium", "Lanthanide", "164.93033", "6", "null"},
            {"68", "Er", "Erbium", "Lanthanide", "167.259", "6", "null"},
            {"69", "Tm", "Thulium", "Lanthanide", "168.93422", "6", "null"},
            {"70", "Yb", "Ytterbium", "Lanthanide", "173.045", "6", "null"},
            {"71", "Lu", "Lutetium", "Lanthanide", "174.9668", "6", "null"},

            {"72", "Hf", "Hafnium", "Transition metal", "178.49", "6", "4"},
            {"73", "Ta", "Tantalum", "Transition metal", "180.94788", "6", "5"},
            {"74", "W", "Tungsten", "Transition metal", "183.84", "6", "6"},
            {"75", "Re", "Rhenium", "Transition metal", "186.207", "6", "7"},
            {"76", "Os", "Osmium", "Transition metal", "190.23", "6", "8"},
            {"77", "Ir", "Iridium", "Transition metal", "192.217", "6", "9"},
            {"78", "Pt", "Platinum", "Transition metal", "195.084", "6", "10"},
            {"79", "Au", "Gold", "Transition metal", "196.966569", "6", "11"},
            {"80", "Hg", "Mercury", "Transition metal", "200.592", "6", "12"},
            {"81", "Tl", "Thallium", "Post-transition metal", "204.38", "6", "13"},
            {"82", "Pb", "Lead", "Post-transition metal", "207.2", "6", "14"},
            {"83", "Bi", "Bismuth", "Post-transition metal", "208.98040", "6", "15"},
            {"84", "Po", "Polonium", "Post-transition metal", "209", "6", "16"},
            {"85", "At", "Astatine", "Metalloid", "210", "6", "17"},
            {"86", "Rn", "Radon", "Noble gas", "222", "6", "18"},

            {"87", "Fr", "Francium", "Alkali metal", "223", "7", "1"},
            {"88", "Ra", "Radium", "Alkaline earth metal", "226", "7", "2"},
            {"89", "Ac", "Actinium", "Actinide", "227", "7", "null"},
            {"90", "Th", "Thorium", "Actinide", "232.0377", "7", "null"},
            {"91", "Pa", "Protactinium", "Actinide", "231.03588", "7", "null"},
            {"92", "U", "Uranium", "Actinide", "238.02891", "7", "null"},
            {"93", "Np", "Neptunium", "Actinide", "237", "7", "null"},
            {"94", "Pu", "Plutonium", "Actinide", "244", "7", "null"},
            {"95", "Am", "Americium", "Actinide", "243", "7", "null"},
            {"96", "Cm", "Curium", "Actinide", "247", "7", "null"},
            {"97", "Bk", "Berkelium", "Actinide", "247", "7", "null"},
            {"98", "Cf", "Californium", "Actinide", "251", "7", "null"},
            {"99", "Es", "Einsteinium", "Actinide", "252", "7", "null"},
            {"100", "Fm", "Fermium", "Actinide", "257", "7", "null"},
            {"101", "Md", "Mendelevium", "Actinide", "258", "7", "null"},
            {"102", "No", "Nobelium", "Actinide", "259", "7", "null"},
            {"103", "Lr", "Lawrencium", "Actinide", "266", "7", "null"},

            {"104", "Rf", "Rutherfordium", "Transition metal", "267", "7", "4"},
            {"105", "Db", "Dubnium", "Transition metal", "268", "7", "5"},
            {"106", "Sg", "Seaborgium", "Transition metal", "269", "7", "6"},
            {"107", "Bh", "Bohrium", "Transition metal", "270", "7", "7"},
            {"108", "Hs", "Hassium", "Transition metal", "269", "7", "8"},
            {"109", "Mt", "Meitnerium", "Unknown", "278", "7", "9"},
            {"110", "Ds", "Darmstadtium", "Unknown", "281", "7", "10"},
            {"111", "Rg", "Roentgenium", "Unknown", "282", "7", "11"},
            {"112", "Cn", "Copernicium", "Transition metal", "285", "7", "12"},
            {"113", "Nh", "Nihonium", "Unknown", "286", "7", "13"},
            {"114", "Fl", "Flerovium", "Post-transition metal", "289", "7", "14"},
            {"115", "Mc", "Moscovium", "Unknown", "290", "7", "15"},
            {"116", "Lv", "Livermorium", "Unknown", "293", "7", "16"},
            {"117", "Ts", "Tennessine", "Unknown", "294", "7", "17"},
            {"118", "Og", "Oganesson", "Unknown", "294", "7", "18"}
    };
}