package studio.brunocasamassa.ajudaquioficial.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import studio.brunocasamassa.ajudaquioficial.fragments.GrupoAbertoMeusPedidosFragment;
import studio.brunocasamassa.ajudaquioficial.fragments.GrupoAbertoPedidosFragment;
import studio.brunocasamassa.ajudaquioficial.fragments.GrupoAbertoRankingFragment;


public class GrupoAbertoTabAdapter extends FragmentStatePagerAdapter {

    private String[] tituloAbas = {"PEDIDOS", "RANKING", "MEUS PEDIDOS"};

    public GrupoAbertoTabAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new GrupoAbertoPedidosFragment();
                break;
            case 1:
                fragment = new GrupoAbertoRankingFragment();
               //notifyDataSetChanged();
                break;
            case 2:
                fragment = new GrupoAbertoMeusPedidosFragment();
                //notifyDataSetChanged();
                break;
        }

        return fragment;

    }


    @Override
    public int getCount() {
        return tituloAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tituloAbas[position];
    }






}