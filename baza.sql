PGDMP  +    )                 }         	   diplomski    17.2    17.2 8    0           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            1           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            2           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            3           1262    41017 	   diplomski    DATABASE        CREATE DATABASE diplomski WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Croatian_Croatia.1250';
    DROP DATABASE diplomski;
                  	   diplomski    false                        2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
                     pg_database_owner    false            4           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                        pg_database_owner    false    4            �            1259    41136    predaje_predaje_id_seq    SEQUENCE        CREATE SEQUENCE public.predaje_predaje_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 -   DROP SEQUENCE public.predaje_predaje_id_seq;
       public               postgres    false    4            �            1259    41064    predaje    TABLE     �   CREATE TABLE public.predaje (
    predaje_id integer DEFAULT nextval('public.predaje_predaje_id_seq'::regclass) NOT NULL,
    kolegij_id integer,
    profesor_id integer,
    akademska_godina character varying(9)
);
    DROP TABLE public.predaje;
       public         heap r       postgres    false    229    4            �            1259    41063    kolegiji_kolegij_id_seq    SEQUENCE     �   CREATE SEQUENCE public.kolegiji_kolegij_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.kolegiji_kolegij_id_seq;
       public               postgres    false    4    226            5           0    0    kolegiji_kolegij_id_seq    SEQUENCE OWNED BY     R   ALTER SEQUENCE public.kolegiji_kolegij_id_seq OWNED BY public.predaje.predaje_id;
          public               postgres    false    225            �            1259    41046    kolegiji    TABLE     �   CREATE TABLE public.kolegiji (
    kolegij_id integer DEFAULT nextval('public.kolegiji_kolegij_id_seq'::regclass) NOT NULL,
    naziv_kolegija character varying(100),
    ects_bodovi integer
);
    DROP TABLE public.kolegiji;
       public         heap r       postgres    false    225    4            �            1259    41032    kontaktpodaci    TABLE     '  CREATE TABLE public.kontaktpodaci (
    kontakt_id integer NOT NULL,
    student_id integer,
    broj_telefona character varying(15),
    adresa text,
    grad character varying(50),
    postanski_broj character varying(10),
    drzava character varying(50),
    email character varying(255)
);
 !   DROP TABLE public.kontaktpodaci;
       public         heap r       postgres    false    4            �            1259    41031    kontaktpodaci_kontakt_id_seq    SEQUENCE     �   CREATE SEQUENCE public.kontaktpodaci_kontakt_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 3   DROP SEQUENCE public.kontaktpodaci_kontakt_id_seq;
       public               postgres    false    220    4            6           0    0    kontaktpodaci_kontakt_id_seq    SEQUENCE OWNED BY     ]   ALTER SEQUENCE public.kontaktpodaci_kontakt_id_seq OWNED BY public.kontaktpodaci.kontakt_id;
          public               postgres    false    219            �            1259    41117    ocjene    TABLE     �   CREATE TABLE public.ocjene (
    ocjena_id integer NOT NULL,
    student_id integer,
    predaje_id integer,
    ocjena integer,
    datum_ocjenjivanja date,
    CONSTRAINT ocjene_ocjena_check CHECK (((ocjena >= 1) AND (ocjena <= 5)))
);
    DROP TABLE public.ocjene;
       public         heap r       postgres    false    4            �            1259    41116    ocjene_ocjena_id_seq    SEQUENCE     �   CREATE SEQUENCE public.ocjene_ocjena_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.ocjene_ocjena_id_seq;
       public               postgres    false    4    228            7           0    0    ocjene_ocjena_id_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE public.ocjene_ocjena_id_seq OWNED BY public.ocjene.ocjena_id;
          public               postgres    false    227            �            1259    41045    predmeti_predmet_id_seq    SEQUENCE     �   CREATE SEQUENCE public.predmeti_predmet_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.predmeti_predmet_id_seq;
       public               postgres    false    222    4            8           0    0    predmeti_predmet_id_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.predmeti_predmet_id_seq OWNED BY public.kolegiji.kolegij_id;
          public               postgres    false    221            �            1259    41055 	   profesori    TABLE     �   CREATE TABLE public.profesori (
    profesor_id integer NOT NULL,
    ime character varying(255),
    prezime character varying(255),
    email character varying(255),
    titula character varying(255)
);
    DROP TABLE public.profesori;
       public         heap r       postgres    false    4            �            1259    41054    profesori_profesor_id_seq    SEQUENCE     �   CREATE SEQUENCE public.profesori_profesor_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 0   DROP SEQUENCE public.profesori_profesor_id_seq;
       public               postgres    false    4    224            9           0    0    profesori_profesor_id_seq    SEQUENCE OWNED BY     W   ALTER SEQUENCE public.profesori_profesor_id_seq OWNED BY public.profesori.profesor_id;
          public               postgres    false    223            �            1259    41019    studenti    TABLE     (  CREATE TABLE public.studenti (
    student_id integer NOT NULL,
    jmbag character(11) NOT NULL,
    ime character varying(50),
    prezime character varying(50),
    oib character(11),
    datum_rodenja date,
    spol character(1),
    upisna_godina integer,
    dug numeric(10,2) DEFAULT 0
);
    DROP TABLE public.studenti;
       public         heap r       postgres    false    4            �            1259    41018    studenti_student_id_seq    SEQUENCE     �   CREATE SEQUENCE public.studenti_student_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.studenti_student_id_seq;
       public               postgres    false    218    4            :           0    0    studenti_student_id_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.studenti_student_id_seq OWNED BY public.studenti.student_id;
          public               postgres    false    217            s           2604    41035    kontaktpodaci kontakt_id    DEFAULT     �   ALTER TABLE ONLY public.kontaktpodaci ALTER COLUMN kontakt_id SET DEFAULT nextval('public.kontaktpodaci_kontakt_id_seq'::regclass);
 G   ALTER TABLE public.kontaktpodaci ALTER COLUMN kontakt_id DROP DEFAULT;
       public               postgres    false    219    220    220            w           2604    41120    ocjene ocjena_id    DEFAULT     t   ALTER TABLE ONLY public.ocjene ALTER COLUMN ocjena_id SET DEFAULT nextval('public.ocjene_ocjena_id_seq'::regclass);
 ?   ALTER TABLE public.ocjene ALTER COLUMN ocjena_id DROP DEFAULT;
       public               postgres    false    228    227    228            u           2604    41058    profesori profesor_id    DEFAULT     ~   ALTER TABLE ONLY public.profesori ALTER COLUMN profesor_id SET DEFAULT nextval('public.profesori_profesor_id_seq'::regclass);
 D   ALTER TABLE public.profesori ALTER COLUMN profesor_id DROP DEFAULT;
       public               postgres    false    224    223    224            q           2604    41145    studenti student_id    DEFAULT     z   ALTER TABLE ONLY public.studenti ALTER COLUMN student_id SET DEFAULT nextval('public.studenti_student_id_seq'::regclass);
 B   ALTER TABLE public.studenti ALTER COLUMN student_id DROP DEFAULT;
       public               postgres    false    217    218    218            &          0    41046    kolegiji 
   TABLE DATA           K   COPY public.kolegiji (kolegij_id, naziv_kolegija, ects_bodovi) FROM stdin;
    public               postgres    false    222    C       $          0    41032    kontaktpodaci 
   TABLE DATA           {   COPY public.kontaktpodaci (kontakt_id, student_id, broj_telefona, adresa, grad, postanski_broj, drzava, email) FROM stdin;
    public               postgres    false    220   eE       ,          0    41117    ocjene 
   TABLE DATA           _   COPY public.ocjene (ocjena_id, student_id, predaje_id, ocjena, datum_ocjenjivanja) FROM stdin;
    public               postgres    false    228   �I       *          0    41064    predaje 
   TABLE DATA           X   COPY public.predaje (predaje_id, kolegij_id, profesor_id, akademska_godina) FROM stdin;
    public               postgres    false    226   _U       (          0    41055 	   profesori 
   TABLE DATA           M   COPY public.profesori (profesor_id, ime, prezime, email, titula) FROM stdin;
    public               postgres    false    224   �V       "          0    41019    studenti 
   TABLE DATA           q   COPY public.studenti (student_id, jmbag, ime, prezime, oib, datum_rodenja, spol, upisna_godina, dug) FROM stdin;
    public               postgres    false    218   QX       ;           0    0    kolegiji_kolegij_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.kolegiji_kolegij_id_seq', 1, false);
          public               postgres    false    225            <           0    0    kontaktpodaci_kontakt_id_seq    SEQUENCE SET     J   SELECT pg_catalog.setval('public.kontaktpodaci_kontakt_id_seq', 1, true);
          public               postgres    false    219            =           0    0    ocjene_ocjena_id_seq    SEQUENCE SET     D   SELECT pg_catalog.setval('public.ocjene_ocjena_id_seq', 522, true);
          public               postgres    false    227            >           0    0    predaje_predaje_id_seq    SEQUENCE SET     E   SELECT pg_catalog.setval('public.predaje_predaje_id_seq', 16, true);
          public               postgres    false    229            ?           0    0    predmeti_predmet_id_seq    SEQUENCE SET     E   SELECT pg_catalog.setval('public.predmeti_predmet_id_seq', 8, true);
          public               postgres    false    221            @           0    0    profesori_profesor_id_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('public.profesori_profesor_id_seq', 1, false);
          public               postgres    false    223            A           0    0    studenti_student_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.studenti_student_id_seq', 15, true);
          public               postgres    false    217            �           2606    41069    predaje kolegiji_pkey 
   CONSTRAINT     [   ALTER TABLE ONLY public.predaje
    ADD CONSTRAINT kolegiji_pkey PRIMARY KEY (predaje_id);
 ?   ALTER TABLE ONLY public.predaje DROP CONSTRAINT kolegiji_pkey;
       public                 postgres    false    226            �           2606    41039     kontaktpodaci kontaktpodaci_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY public.kontaktpodaci
    ADD CONSTRAINT kontaktpodaci_pkey PRIMARY KEY (kontakt_id);
 J   ALTER TABLE ONLY public.kontaktpodaci DROP CONSTRAINT kontaktpodaci_pkey;
       public                 postgres    false    220            �           2606    41123    ocjene ocjene_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY public.ocjene
    ADD CONSTRAINT ocjene_pkey PRIMARY KEY (ocjena_id);
 <   ALTER TABLE ONLY public.ocjene DROP CONSTRAINT ocjene_pkey;
       public                 postgres    false    228            �           2606    41053    kolegiji predmeti_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.kolegiji
    ADD CONSTRAINT predmeti_pkey PRIMARY KEY (kolegij_id);
 @   ALTER TABLE ONLY public.kolegiji DROP CONSTRAINT predmeti_pkey;
       public                 postgres    false    222            �           2606    41141    profesori profesori_email_key 
   CONSTRAINT     Y   ALTER TABLE ONLY public.profesori
    ADD CONSTRAINT profesori_email_key UNIQUE (email);
 G   ALTER TABLE ONLY public.profesori DROP CONSTRAINT profesori_email_key;
       public                 postgres    false    224            �           2606    41060    profesori profesori_pkey 
   CONSTRAINT     _   ALTER TABLE ONLY public.profesori
    ADD CONSTRAINT profesori_pkey PRIMARY KEY (profesor_id);
 B   ALTER TABLE ONLY public.profesori DROP CONSTRAINT profesori_pkey;
       public                 postgres    false    224            z           2606    41026    studenti studenti_jmbag_key 
   CONSTRAINT     W   ALTER TABLE ONLY public.studenti
    ADD CONSTRAINT studenti_jmbag_key UNIQUE (jmbag);
 E   ALTER TABLE ONLY public.studenti DROP CONSTRAINT studenti_jmbag_key;
       public                 postgres    false    218            |           2606    41028    studenti studenti_oib_key 
   CONSTRAINT     S   ALTER TABLE ONLY public.studenti
    ADD CONSTRAINT studenti_oib_key UNIQUE (oib);
 C   ALTER TABLE ONLY public.studenti DROP CONSTRAINT studenti_oib_key;
       public                 postgres    false    218            ~           2606    41024    studenti studenti_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.studenti
    ADD CONSTRAINT studenti_pkey PRIMARY KEY (student_id);
 @   ALTER TABLE ONLY public.studenti DROP CONSTRAINT studenti_pkey;
       public                 postgres    false    218            �           2606    41070     predaje kolegiji_predmet_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.predaje
    ADD CONSTRAINT kolegiji_predmet_id_fkey FOREIGN KEY (kolegij_id) REFERENCES public.kolegiji(kolegij_id);
 J   ALTER TABLE ONLY public.predaje DROP CONSTRAINT kolegiji_predmet_id_fkey;
       public               postgres    false    222    226    4738            �           2606    41075 !   predaje kolegiji_profesor_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.predaje
    ADD CONSTRAINT kolegiji_profesor_id_fkey FOREIGN KEY (profesor_id) REFERENCES public.profesori(profesor_id);
 K   ALTER TABLE ONLY public.predaje DROP CONSTRAINT kolegiji_profesor_id_fkey;
       public               postgres    false    4742    224    226            �           2606    41040 +   kontaktpodaci kontaktpodaci_student_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.kontaktpodaci
    ADD CONSTRAINT kontaktpodaci_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.studenti(student_id);
 U   ALTER TABLE ONLY public.kontaktpodaci DROP CONSTRAINT kontaktpodaci_student_id_fkey;
       public               postgres    false    220    4734    218            �           2606    41129    ocjene ocjene_predaje_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.ocjene
    ADD CONSTRAINT ocjene_predaje_id_fkey FOREIGN KEY (predaje_id) REFERENCES public.predaje(predaje_id);
 G   ALTER TABLE ONLY public.ocjene DROP CONSTRAINT ocjene_predaje_id_fkey;
       public               postgres    false    228    226    4744            �           2606    41124    ocjene ocjene_student_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.ocjene
    ADD CONSTRAINT ocjene_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.studenti(student_id);
 G   ALTER TABLE ONLY public.ocjene DROP CONSTRAINT ocjene_student_id_fkey;
       public               postgres    false    4734    218    228            &   U  x��TK��0]�N�%�TQ����3�(X��;)Y���%/r�,8a5s/ZN�$�f�R�u����*`��Z��~gP�C��(�	��+sQ�[���]�2䌉��g��,���p���}�ɿ.���[�544��˸9�6d��
Zn�}{�I6���H��P*�
�S̈́:&�06t�}8e{dGP�B�+��@�N7h���w��Ur&�d����1�ai5[��ә@�lV���!�Ar�k�h2M��[�1�(� ��P�F�SQL�R{�V>��N����5ك�I.��~������vo�X���I�S��=�!�Y�ox��-�s�k.�_�t�R���ĞZ�?��ޥ��~O�
5���v�3Zn�I-s����-�\G.Y�WF͞�b1�|��~��\�Cv��U2V��V,2�9��}
���y�Ο��mC�2B�M���{G��Ȫ�p�f�{�䴴�+�����-+ǰ��k�\�G�L`���I_N���!�ېɞ�9�^^\<ψ#�(;�~̚?V�.y:vL��F�ƷK&�xx!�CP�b�K< �U�b�*~[xG����|�*Yl�v������'�'�\      $   e  x�u��N�H�ח�������xLkzDwzX�fsC�P����#5/0+�͋�
�kN٦�31B�D���9߹$h����*���?ss�����&�-;��7�9�!����Z����K.؅����>|p'���[�։T}u�`���_�����3j�����R�^K��Z�Z�(�kޖ��)�@D3�>X��tW���;[�DyȈ(^�Eף\�h�M�i���H!��+@@bR4L�Ԃ>W&�)1����q�?wwj�:�6�TL*~��j�m�[SL�?Z���z̹�����F���0r�D����������h5̺+�ԏ�;.��Ěn���I�C�=b\(������0o�#���njv/O�� �7e6��r�����K(��cXj�k�C!���0��`�wNh��&�ē��ՄD�K���\:���wzqH��-;���JQ#$�󃵽� �1ã�հ�+�QZ��ㅢ[����֔ɉhj+�{��[�F[��(Q�^����?���g�W7���^2v9����E(QI��	}�T�1�fq�u&k��#RI����֓~�>o_�3ҵ�s*�|���A[��G�׽�S���H�N5��(MFr�{or�sc��K$p���2��u�UW�����C��TqE�#&1>~C˙E�>9�q�-L�s��4a�e�Q�tԬ�+v�=[A2�r] >��<����7gP��eR�Rǝ��/L�t�K9�fAlz�]�"�3���s����sߡ�BΚ]SA�W���`͂��h���� X� �)���zM_���M�K���Tv��:
�����T���i.��s�N7,#�#h��Cל��g��R�v���@���N�c�#rtήFO�%#�ޮ�9̙Cl�#\����j$O�e���WcD=P�N�V$G��B�,g�A��ToMr���)6�Yf7�w��z����;s�pBrD��ϝRvpg/��3-[V#n$]���v�}��>�M{X���;4��Z]�k��4�q.oq�L��ʌ%�'B�!��I�8|��-�\9-��F��0w��ok�=E�8;��H�8���y����}b�=�����
ONN�^9Z:      ,   u  x�m�k�&)DW�G��k/��uDT��ѣn�tї�;�i����y����럟��?M��翿M�K��yp}���4�4�o�������������Ӵ��ܿ}����Y;m��oc~v��y��]{||v!,���g����r}vA�%���E�?�}6�|x:�iv����ð����|mg��猃��?�Ͻ��~<Ϯ�/�?Ϟ�x��������_ �i;�lR����'f�p���Ϧ�m����v6/.G�����]���N[|� {�=m�(zxO�%�|x��z��ˑ�����#����bqU~�;�nP<�[_6������֟�2��r���y9��q$���g�Q޿�;���ṙ��o^�Y�4/�|��_oH0��_���Y���X��N��˱��=Y}9ֻ�����wM��(�;��1w��~��5���{�{d߸��˰Y�����ހ>#�ۻ
��Kb�]���E��*�kJM	�1)5%��]�} �%�'�]~"�%��o�*)?A$9?A$I?A$Y?A$i?A$y?u�l��d��������$��G{�d����������(��H����D"D�D"D�;yp!� %� )� -� 1� 5�$�k`E�(�k�U�,�k�e�0�k u�4�k`�0䲉F��D%�wBT����[u�~�_
�,U)��&֐1.��6.���C�j,��wf���g���́�ߕ<��$��:g4ڄ".��F�e�h^͹0�J��:_�����C����{��`<���S���S(`<_�;���9p/` ��+y��f��/غ�$l���߻�0��ҝ���7�	o\�
�!� C�Eq�:�R��g'a*|JL�V�|��� c�E`N��>e�a.�~�ǰ�$�a0�(���`[�t1�_���߻�0��_T���l��~a7|WA7��je�`9�ɪ�sD�ǌ�tD{�=������vD�U��(��h���:��h�2X�23�.�Ѭ��&Z��h�#^�sg�&� R����V��yX��w����K���D16g�p!�r���D�Qr� !�#!{'!y+!Q/)�	3�en$���H��vf?���5�!�.QG]��$��$<I庣���3����U��ݰ%Q��� 6�(�/�W�n�_���×Ĭ���%1�H=*|I̪�_�*�×ī}ǁ:|I���
�Uw��K�(י/|I��o_���
�$D��$vU{�/	Q.�/	�;_�\_����J�$[�y�/�&+	_�Ew��KJ�w%�K�d�`�j�:󴪿��8M(p����h��I����h�C8S��!p)Z�����FQ�g��xu���R�՝K�k�����,Zr�bY��!@�(�a��fQ�Æ��D���
�'�,s��E�*ힰ��e��7�޼�5'���w~C���cO)ǟ�!G=z���(�r&�I9eQ;)KuV��ȐwRN��NJ)���r��;)W=���r	� �J� �\��";����C>H��A䃔��D>H��Q��]�P�&B5@��!�eo�8�	���y�����������da'(�ə4A�U�&)]N�IJћ�tQ�EJ��i���dZ�9�)ŕ�"e��H����!q�H�r>-RJY拔)'�&�f�I�����]�M�.��&��a�I��ڤ�rPmRٴM�!��I)�?�H)�%)G]�h�����FJ�ډFJQ�h���FJQ�h��;�h��K�h�\��
#��V)W��0Ra����
#�GW)w=��H���@9D}�@9�P(GJ�hB�R3��������א���:�;$.?�Tiq���91���2�۱_�j^ƭ��6��[�m�/G.��yAJ��Rz=/#H)+���"H)&+��R�E�RJ�RJ�IJ��HRJ�IJ)�"I���*I)�[$)�t�$��n����-��R�E'��o�I)�[tRJ��R�E'�ح褔.:)U;)���NJ)㢓R���B.)���AJ)�b�RTRJ1��R�� ��s1H)�\RJA��R��$��t1I)5]LPN)�b�rJU�Sʺ���R����.��9r5U��ͨC��9����T�;����T�[�ț*x�]��Y���3���Nh�!�pM3�.���|��=AR��"���-Rʍ}lR��&���mR��&���mR��oR���&���ؤt	�MJ�ۤ�*x�H�U�ҫ�e#����FJy���^/)�
^6Rz�l��*x�H)o;��RN�4RF�4R�I�F��z���H)�W)��e)�
^)�
^)���R�w)�
^:)��e:)�`L'�餔!��:A'�N�I)B:)�@H'�餔!��R d�R
�R�zJe���}�$H�k��A����`��=��z)G-2H9j��A�Q�'����O&)GU�LRJ��I�Qe=���Z�LR�j�3IYfr�&��IRN	�$��餜>��S§��y���R�HvRNY�N�)[�I��!I��RJ啝�Ko�rI�R.�AJym���e&�R�USR�z^� ��eR����7���[�a�r��LR��
r�R>"�I�-;I�e&)���$�ح��Y�����Y�����YM��g5�Kz��$/�}V�� �}VJz�%�'��g��
��g5�\7���ǩ������:����:经�~������ZK�]�{�-y��~B��c���ے/��x�u[�y,Ͼ�O"���=�����n-�z�1<㶖Gz�_����y���%%oo���%oo��g~��Y��M�]�;��R���D1wj�NBC?Fg������io�X��X+�a��~���jX��]����W��=�OLt�����1���N{�����i�*k���FJ7f�A���k�wG`����o'T������49�      *   y  x�]�An1��ǴkK�����(�K� A �"҈�z���zW|�'��J�@R'꒺�4�ި�_��������r+�(��\ :r�h�1���86un ����?8 G�p��K�� ����"Q���<ӕ$�Ҫ��t��ITy��[T�!Q��2�xIL:`��"�c �6�H��=&�D��t���tRץ��t.�N�~�^����"����h6�.��-[>/��Z�ݶjM]�0�m�
[�0�t�J&_��&Q��;���{ITz��A��$Q�L�I� 1�=�]$*�qYxһI�>������tCړ�I��iO��Ĥ6�t�J7�=�n�n�r�t_�>�HO�L�>�<�$&}�'}�D�����V}�*      (   Y  x�}RKr�0]+����o�k���vʲRp���|��8D�B�UɄ�	;=�d��B��6��:*!!�[.�7�#���baЅ���;���J f���ܚo��DGFz�l���qt<�N�2yA�H?D�=~������\ �ԚMt�3���T������PYaL7-�k�YJ!Z�V!#x�k��?O�f嶝�#�&�R��p'e�'�*,��l*Z��=<&?'�������顕U��:��l����٣�Kڱ�����2�C�W�WqJ�&��>����LY��� �ƅ`��N�˒E>D��{䆗C�$W;���97�.yruê�|�0��#L      "   �  x�e�Kr7���]���si���JT^eG,׈'5&y�l�;8�W�� 03�J+J�����n
 �|��Ż}i�����O1
��w:v���@A8�$6� ~9��xNi�TS�v�6�x��^`P%5��������xJ������i��ַ��VHӔ��?n�x�j��r�i�H��W�
Aڦ܈�Ø����OS�r=��;���`A#�,@��^۟��s�Z���^��� 
��M5^|/����z��ێ����N�F�rj�AܤOk�@ ��-yeAƦܰ}�����v8��� �l�
^��"b����E� r��hF��Lg��/�������z�L&�-��l꽸K�n�`��F0�@R�I�F p�͋x���Z"� ��(=��Q`$�F"��gy�ڿ�b�:��Hx`=;�c�cn��������E�,y����5��롺�o��r$���(�x^�/�t�D�V8K�ۜ14�	�� ��+#�ߜ�o�@U�]Frb�Y"6���f^p0S�	�#��^@d�?�l��w"�Q�@h�t`6�c�|��j���(�DlT�x�cy��O��(LX�O�v�y�DjB��e���tHX.-B���h�(nYcȈ�b]��̑�z�!���-�z�%��u�����)^p&6�a��{�?l��u�r&q>�` _yl����&��7��/�K�G�\��p��2��~[��T��Ǧ��A���ؠɾ>f*8����Z��C��[�{������s�=?�O����HxEh��z����P����x�w/��D7}6a�w��M��6��gq7�����G���01�M�ll����a��Py�G��^�c��I�4�9�鴻� ��|q��[��K6�>�����s�_��������8���ߕ��?$��     